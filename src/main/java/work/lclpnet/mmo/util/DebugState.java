package work.lclpnet.mmo.util;

public class DebugState {

	private static boolean debug = false;

	public static void setDebug(boolean debug) {
		DebugState.debug = debug;
	}
	
	public static boolean isDebug() {
		return debug;
	}
	
}
