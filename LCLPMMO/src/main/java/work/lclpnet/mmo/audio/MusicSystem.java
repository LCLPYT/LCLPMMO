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
import work.lclpnet.mmo.util.FFMPEG;
import work.lclpnet.mmo.util.MessageUtils;

@OnlyIn(Dist.CLIENT)
public class MusicSystem {

	private static final Map<String, MusicInstance> playing = new HashMap<>();

	public static void play(String path, Consumer<ITextComponent> feedback) {
		File file = getMusicFile(path);
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

	private static boolean tryConvert(File file, Consumer<ITextComponent> feedback) {
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
					Minecraft.getInstance().player.sendChatMessage(String.format("/music play %s", output.getName()));
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

	public static void setVolume(String path, float perc, Consumer<ITextComponent> feedback) {
		File file = getMusicFile(path);
		if(!checkFilePlaying(feedback, file)) return;

		perc = MathHelper.clamp(perc, 0F, 1F);

		MusicInstance music = getMusic(file);
		if(music != null) music.setVolume(perc);
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
		File file = getMusicFile(path);
		if(!checkFilePlaying(feedback, file)) return;

		MusicInstance music = getMusic(file);
		if(music != null) music.stop();
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

	static void untrack(File file) {
		playing.remove(file.getPath());
	}

	private static boolean checkFileExists(Consumer<ITextComponent> feedback, File file) {
		if(!file.exists()) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.file_not_exist", file.getPath()), MessageType.ERROR));
			return false;
		}
		return true;
	}

	private static boolean checkFilePlaying(Consumer<ITextComponent> feedback, File file) {
		if(!playing.containsKey(file.getPath())) {
			feedback.accept(LCLPMMO.TEXT.message(I18n.format("music.play.file_not_playing", file.getPath()), MessageType.ERROR));
			return false;
		}
		return true;
	}

	private static File getMusicFile(String path) {
		File file = new File(getMusicDir(), path);
		return file;
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

}
