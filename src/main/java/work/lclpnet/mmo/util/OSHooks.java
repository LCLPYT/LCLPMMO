package work.lclpnet.mmo.util;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ALL")
public class OSHooks {

	private static class OSHandler {

		public File getFFMPEGExecutable() {
			return new File("bin" + File.separatorChar + "ffmpeg" + File.separatorChar + "ffmpeg" + File.separatorChar + "ffmpeg");
		}
		
		public boolean isFFMPEGLocalInstalled() {
			return getFFMPEGExecutable().exists();
		}
		
		public File getYTDLExecutable() {
			return new File("bin", "youtube-dl");
		}

		public boolean makeExecutable(File rel) {
			ProcessBuilder pb = new ProcessBuilder("chmod", "+x", rel.getAbsolutePath());
			pb.inheritIO();
			try {
				Process p = pb.start();
				if(p.waitFor() == 0) return true;
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}
	}

    private static class LinuxHandler extends OSHandler {
        // Override methods here
    }

    private static class WinHandler extends OSHandler {

    	@Override
    	public File getFFMPEGExecutable() {
    		return new File("bin" + File.separatorChar + "ffmpeg" + File.separatorChar + "ffmpeg" + File.separatorChar + "bin" + File.separatorChar + "ffmpeg.exe");
    	}
    	
    	@Override
    	public File getYTDLExecutable() {
    		return new File("bin", "youtube-dl.exe");
    	}

		@Override
		public boolean makeExecutable(File rel) {
			return true;
		}
	}

    private static final OSHandler handler;

    static {
        if(System.getProperty("os.name").equalsIgnoreCase("Linux")) handler = new LinuxHandler();
        else if(System.getProperty("os.name").contains("Windows")) handler = new WinHandler();
        else handler = new OSHandler();
    }
    
    public static boolean isFFMPEGLocalInstalled() {
    	return handler.isFFMPEGLocalInstalled();
    }
    
    public static File getFFMPEGExecutable() {
    	return handler.getFFMPEGExecutable();
    }
    
    public static File getYTDLExecutable() {
    	return handler.getYTDLExecutable();
    }

	public static boolean makeExecutable(File rel) {
    	return handler.makeExecutable(rel);
	}

}
