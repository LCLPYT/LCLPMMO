package work.lclpnet.mmo.util;

import java.io.File;

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

}
