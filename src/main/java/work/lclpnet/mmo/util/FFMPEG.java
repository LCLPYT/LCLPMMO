package work.lclpnet.mmo.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FFMPEG {

	private static Boolean inPath = null, local = null;

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isInstalled() {
		if(inPath == null && local == null) {
			local = isLocalInstalled();
			inPath = isInPath();
		}
		if(inPath != null && inPath.booleanValue()) return true;
		else return local != null && local.booleanValue();
	}

	public static boolean isLocalInstalled() {
		return OSHooks.isFFMPEGLocalInstalled();
	}

	public static boolean isInPath() {
		try {
			Runtime.getRuntime().exec("ffmpeg");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static File getFFMPEGExecutable() {
		if(!isLocalInstalled()) return null;
		return OSHooks.getFFMPEGExecutable();
	}

	private static void execFFMPEGCommand(Consumer<Integer> callback, String... args) throws IOException {
		if(!isInstalled()) return;

		String program;
		if(local != null && local.booleanValue()) {
			File rel = getFFMPEGExecutable();
			if(rel == null) return;
			if(!OSHooks.makeExecutable(rel)) {
				System.err.println("Could not access ffmpeg.");
				return;
			}
			program = rel.getAbsolutePath();
		}
		else if(inPath != null && inPath.booleanValue()) program = "ffmpeg";
		else return;

		String[] procArgs = new String[args.length + 1];
		procArgs[0] = program.toString();
		for(int i = 0; i < args.length; i++) procArgs[i + 1] = args[i];

		System.out.printf("Executing %s\n", Arrays.stream(procArgs).map(a -> a + " ").collect(Collectors.joining()));

		ProcessBuilder pb = new ProcessBuilder(procArgs);
		pb.inheritIO();
		final Process p = pb.start();

		new Thread(() -> {
			try {
				int exitCode = p.waitFor();
				System.out.println("FFMPEG process finished with exit code " + exitCode + ".");
				callback.accept(exitCode);
			} catch (InterruptedException e) {
				e.printStackTrace();
				callback.accept(null);
			}
		}).start();
	}

	public static void convertToWav(File file, File output, Consumer<Integer> handler) throws IOException {
		execFFMPEGCommand(handler, "-i", file.getAbsolutePath(), "-acodec", "pcm_s16le", output.getAbsolutePath());
	}

}
