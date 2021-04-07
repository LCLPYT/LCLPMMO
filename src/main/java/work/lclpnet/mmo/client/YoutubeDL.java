package work.lclpnet.mmo.client;

import org.apache.commons.io.FileUtils;
import work.lclpnet.mmo.util.MMOUtils;
import work.lclpnet.mmo.util.OSHooks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class YoutubeDL {

	private static Boolean inPath = null, local = null;
	private static int nextId = 0;

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isInstalled() {
		if(inPath == null && local == null) {
			local = isLocalInstalled();
			inPath = isInPath();
		}
		if(inPath != null && inPath.booleanValue()) return true;
		else return local != null && local.booleanValue();
	}

	private static boolean isLocalInstalled() {
		File exe = OSHooks.getYTDLExecutable();
		if(!exe.exists()) return false;

		return exe.exists() && !exe.isDirectory();
	}

	private static boolean isInPath() {
		try {
			Runtime.getRuntime().exec("youtube-dl");
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private static void execYTDLCommand(Consumer<Integer> callback, String... args) throws IOException {
		if(!isInstalled()) return;

		String program;
		if(local != null && local.booleanValue()) {
			File rel = OSHooks.getYTDLExecutable();
			if(!OSHooks.makeExecutable(rel)) {
				System.err.println("Could not access ytdl.");
				return;
			}
			program = rel.getAbsolutePath();
		}
		else if(inPath != null && inPath.booleanValue()) program = "youtube-dl";
		else return;

		String[] procArgs = new String[args.length + 1];
		procArgs[0] = program;
		for(int i = 0; i < args.length; i++) procArgs[i + 1] = args[i];

		System.out.printf("Executing %s\n", Arrays.stream(procArgs).map(a -> a + " ").collect(Collectors.joining()));

		ProcessBuilder pb = new ProcessBuilder(procArgs);
		pb.inheritIO();
		final Process p = pb.start();

		new Thread(() -> {
			try {
				int exitCode = p.waitFor();
				System.out.println("Youtube-dl process finished with exit code " + exitCode + ".");
				callback.accept(exitCode);
			} catch (InterruptedException e) {
				e.printStackTrace();
				callback.accept(null);
			}
		}).start();
	}

	public static void download(String url, BiConsumer<Integer, File> handler) throws IOException {
		int current = nextId++;
		final File dir = new File(MMOUtils.getTmpDir(), "dl" + File.separatorChar + current);
		if(!dir.exists()) dir.mkdirs();
		
		if(!FFMPEG.isInstalled()) {
			System.err.println("No ffmpeg installation found.");
			handler.accept(1, null);
			return;
		}
		
		Consumer<Integer> consumer = i -> {
			if(i == null) handler.accept(null, null);
			else if(i == 0) {
				File[] children = dir.listFiles();
				if(children == null || children.length <= 0) {
					handler.accept(1, null);
					System.err.println("Downloaded file not found.");
					return;
				}
				File f = children[0];
				File destFile = new File(MMOUtils.getTmpDir(), "dl" + File.separatorChar + f.getName());
				try {
					FileUtils.copyFile(f, destFile);
					FileUtils.forceDelete(dir);
					destFile.deleteOnExit();
					handler.accept(0, destFile);
				} catch (IOException e) {
					e.printStackTrace();
					handler.accept(1, null);
				}
			} else {
				handler.accept(i, null);
			}
		};
		
		if(FFMPEG.isInPath()) execYTDLCommand(consumer, url, "-o", dir.getAbsolutePath() + File.separatorChar + "%(title)s.%(ext)s", "-x");
		else execYTDLCommand(consumer, url, "-o", dir.getAbsolutePath() + File.separatorChar + "%(title)s.%(ext)s", "-x", "--ffmpeg-location", FFMPEG.getFFMPEGExecutable().getAbsolutePath());
	}
	
	public static void downloadQuery(String url, BiConsumer<Integer, File> handler) throws IOException {
		int current = nextId++;
		final File dir = new File(MMOUtils.getTmpDir(), "dl" + File.separatorChar + current);
		if(!dir.exists()) dir.mkdirs();
		
		if(!FFMPEG.isInstalled()) {
			System.err.println("No ffmpeg installation found.");
			handler.accept(1, null);
			return;
		}
		
		Consumer<Integer> consumer = i -> {
			if(i == null) handler.accept(null, null);
			else if(i == 0) {
				File[] children = dir.listFiles();
				if(children == null || children.length <= 0) {
					handler.accept(1, null);
					System.err.println("Downloaded file not found.");
					return;
				}
				File f = children[0];
				File destFile = new File(MMOUtils.getTmpDir(), "dl" + File.separatorChar + f.getName());
				try {
					FileUtils.copyFile(f, destFile);
					FileUtils.forceDelete(dir);
					destFile.deleteOnExit();
					handler.accept(0, destFile);
				} catch (IOException e) {
					e.printStackTrace();
					handler.accept(1, null);
				}
			} else {
				handler.accept(i, null);
			}
		};
		
		String s = "ytsearch1:" + url;
		if(FFMPEG.isInPath()) execYTDLCommand(consumer, s, "-o", dir.getAbsolutePath() + File.separatorChar + "%(title)s.%(ext)s", "-x");
		else execYTDLCommand(consumer, s, "-o", dir.getAbsolutePath() + File.separatorChar + "%(title)s.%(ext)s", "-x", "--ffmpeg-location", FFMPEG.getFFMPEGExecutable().getAbsolutePath());
	}
	
}
