package work.lclpnet.mmo.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.corebase.util.MessageType;
import work.lclpnet.corebase.util.Substitute;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.EnvironmentUtils;
import work.lclpnet.mmo.util.FFMPEG;
import work.lclpnet.mmo.util.MessageUtils;
import work.lclpnet.mmo.util.YoutubeDL;

@OnlyIn(Dist.CLIENT)
public class MusicSystem {

	private static final Map<String, MusicInstance> playing = new HashMap<>();

	public static void play(String path, Consumer<ITextComponent> feedback) {
		play(getMusicFile(path), feedback);
	}

	public static void play(File file, Consumer<ITextComponent> feedback) {
		if(!checkFileExists(feedback, file)) return;

		String name = file.getName();
		MusicInstance music = null;
		float vol = getVolume();
		if (FilenameUtils.isExtension(name, "mid")) music = new MidiMusic(file, vol);
		else if (FilenameUtils.isExtension(name, "wav")) music = new WavMusic(file, vol);
		else if (tryConvert(file, feedback)) return;

		if(music != null) {
			music.play();
			playing.put(file.getPath(), music);
		} else feedback.accept(LCLPMMO.TEXT.complexMessage(I18n.format("music.incompatible"), TextFormatting.RED, new Substitute(FilenameUtils.getExtension(name), TextFormatting.YELLOW)));
	}

	public static void setVolume(String path, float perc, Consumer<ITextComponent> feedback) {
		if(!volumeIfExists(getMusicFile(path), perc) && !volumeIfExists(getDownloadedMusicFile(path), perc)) 
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.file_not_playing", path), MessageType.ERROR));
	}

	public static void setOverallVolume(float perc, Consumer<ITextComponent> feedback) {
		perc = MathHelper.clamp(perc, 0F, 1F);

		if(playing.isEmpty()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.none_playing"), MessageType.ERROR));
			return;
		}

		final float volume = perc;
		playing.values().forEach(m -> m.setVolume(volume));
	}

	public static void stopSound(String path, Consumer<ITextComponent> feedback) {
		if(!stopIfExists(getMusicFile(path)) && !stopIfExists(getDownloadedMusicFile(path))) 
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.file_not_playing", path), MessageType.ERROR));
	}

	private static boolean stopIfExists(File f) {
		if(!checkFilePlaying(f)) return false;

		MusicInstance music = getMusic(f);
		if(music != null) music.stop();
		return true;
	}

	private static boolean volumeIfExists(File f, float perc) {
		if(!checkFilePlaying(f)) return false;

		perc = MathHelper.clamp(perc, 0F, 1F);

		MusicInstance music = getMusic(f);
		if(music != null) music.setVolume(perc);
		return true;
	}

	public static void stopAllSound(Consumer<ITextComponent> feedback) {
		if(playing.isEmpty()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.none_playing"), MessageType.ERROR));
			return;
		}

		List<MusicInstance> toStop = new ArrayList<>(playing.values());
		toStop.forEach(m -> m.stop());
	}

	public static List<String> getAllMusicFiles() {
		File dir = getMusicDir();
		if(!dir.exists() || dir.list() == null) return new ArrayList<>();
		return Arrays.asList(dir.list());
	}

	public static List<String> getAllPlaying() {
		return playing.keySet().stream().map(key -> FilenameUtils.getName(key)).collect(Collectors.toList());
	}

	public static List<String> getDownloadedVideoTitles() {
		File dir = new File(EnvironmentUtils.getTmpDir(), "dl");
		if(!dir.exists()) return Lists.newArrayList();

		File[] children = dir.listFiles();
		if(children == null || children.length <= 0) return Lists.newArrayList();

		List<String> titles = new ArrayList<>();
		for(File f : children) 
			if(!f.isDirectory()) 
				titles.add(f.getName());

		return titles;
	}

	static void untrack(File file) {
		playing.remove(file.getPath());
	}

	public static boolean tryConvert(File file, Consumer<ITextComponent> feedback) {
		if(!FFMPEG.isInstalled()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("warn.ffmpeg.not_installed"), MessageUtils.WARN));
			return false;
		}
		feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.convert.trying"), MessageType.OTHER));
		File output = new File(file.getParentFile(), file.getName().split("\\.(?=[^\\.]+$)")[0] + ".wav");
		if(output.exists()) return true; //Already converted

		try {
			FFMPEG.convertToWav(file, output, i -> {
				if(i == null || i != 0) feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.convert.error"), MessageType.ERROR));
				else {
					MusicSystem.play(output, feedback);
					feedback.accept(LCLPMMO.TEXT.complexMessage(I18n.format("music.convert.success", "%s"), TextFormatting.GREEN, 
							new Substitute(output.getName(), TextFormatting.YELLOW)));
					System.out.println("Deleting old file...");
					System.out.println(file.delete() ? "Successfully deleted old file" : "Could not delete old file.");
				}
			});
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.convert.error"), MessageType.ERROR));
			return false;
		}
	}

	private static boolean checkFileExists(Consumer<ITextComponent> feedback, File file) {
		if(!file.exists()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.file_not_exist", file.getPath()), MessageType.ERROR));
			return false;
		}
		return true;
	}

	private static boolean checkFilePlaying(File file) {
		return playing.containsKey(file.getPath());
	}

	private static File getMusicFile(String path) {
		return new File(getMusicDir(), path);
	}

	private static File getDownloadedMusicFile(String path) {
		return new File(EnvironmentUtils.getTmpDir(), "dl" + File.separatorChar + path);
	}

	private static File getMusicDir() {
		return new File("music");
	}

	private static MusicInstance getMusic(File file) {
		return playing.get(file.getPath());
	}

	public static float getVolume() {
		GameSettings settings = Minecraft.getInstance().gameSettings;
		return settings.getSoundLevel(SoundCategory.RECORDS) * settings.getSoundLevel(SoundCategory.MASTER);
	}

	public static void playYtUrl(String file, Consumer<ITextComponent> feedback) {
		if(!YoutubeDL.isInstalled()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("warn.ytdl.not_installed"), MessageUtils.WARN));
			return;
		}
		feedback.accept(LCLPMMO.TEXT.complexMessage(I18n.format("music.download.trying", "%s"), TextFormatting.AQUA, new Substitute(file, TextFormatting.YELLOW)));

		try {
			YoutubeDL.download(file, (i, f) -> {
				if(i == null || i != 0 || f == null) {
					feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.error"), MessageType.ERROR));
					return;
				}

				feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.success"), MessageType.SUCCESS));
				MusicSystem.tryConvert(f, feedback);
			});
		} catch (IOException e) {
			e.printStackTrace();
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.error"), MessageType.ERROR));
		}
	}

	public static void playYtSearch(String file, Consumer<ITextComponent> feedback) {
		if(!YoutubeDL.isInstalled()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("warn.ytdl.not_installed"), MessageUtils.WARN));
			return;
		}
		feedback.accept(LCLPMMO.TEXT.complexMessage(I18n.format("music.download.trying", "%s"), TextFormatting.AQUA, new Substitute(file, TextFormatting.YELLOW)));

		try {
			YoutubeDL.downloadQuery(file, (i, f) -> {
				if(i == null || i != 0 || f == null) {
					feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.error"), MessageType.ERROR));
					return;
				}

				feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.success"), MessageType.SUCCESS));
				MusicSystem.tryConvert(f, feedback);
			});
		} catch (IOException e) {
			e.printStackTrace();
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.download.error"), MessageType.ERROR));
		}
	}

	public static void playDownloaded(String file, Consumer<ITextComponent> feedback) {
		play(getDownloadedMusicFile(file), feedback);
	}

}
