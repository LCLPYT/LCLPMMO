package work.lclpnet.mmo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

public class FFMPEG {

	private static Boolean inPath = null, local = null;
	private static String ffmpegLocator = null;

	public static boolean isInstalled() {
		if(inPath == null && local == null) {
			local = isLocalInstalled();
			inPath = isInPath();
		}
		if(inPath != null && inPath.booleanValue()) return true;
		else if(local != null && local.booleanValue()) return true;
		else return false;
	}

	public static boolean isLocalInstalled() {
		File locator = new File("bin" + File.separatorChar + "ffmpeg", ".locator");
		if(!locator.exists()) return false;

		String base64;
		try (InputStream in = new FileInputStream(locator);
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out);
			base64 = new String(out.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		String decoded = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
		ffmpegLocator = decoded;
		File ffmpeg = new File("bin" + File.separatorChar + "ffmpeg" + File.separatorChar + ffmpegLocator + File.separatorChar + "bin" + File.separatorChar + "ffmpeg.exe");
		return ffmpeg.exists();
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
		return new File("bin" + File.separatorChar + "ffmpeg" + File.separatorChar + ffmpegLocator + File.separatorChar + "bin" + File.separatorChar + "ffmpeg.exe");
	}

	private static void execFFMPEGCommand(Consumer<Integer> callback, String... args) throws IOException {
		if(!isInstalled()) return;

		String program;
		if(local != null && local.booleanValue()) {
			File rel = getFFMPEGExecutable();
			if(rel == null) return;
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
