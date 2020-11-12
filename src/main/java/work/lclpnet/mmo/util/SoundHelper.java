package work.lclpnet.mmo.util;

import java.util.Random;

public class SoundHelper {

	private static final Random ran = new Random();
	
	public static float randomPitch(float min, float max) {
		return min + ran.nextFloat() * (max - min);
	}
	
}
