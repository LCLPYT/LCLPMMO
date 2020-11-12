package work.lclpnet.mmo.util;

import net.minecraft.util.math.MathHelper;

public class Color {

	public static final int WHITE = 0xffffffff,
			BLACK = 0xff000000,
			RED = 0xffff0000,
			GREEN = 0xff00ff00,
			BLUE = 0xff0000ff,
			YELLOW = 0xffffff00,
			PINK = 0xffff00ff,
			CYAN = 0xff00ffff,
			ORANGE = 0xffff7f00,
			AQUA = 0xff007fff,
			PURPLE = 0xff7f00ff,
			MAGENTA = 0xffff007f,
			LIME = 0xff7fff00,
			TURQUOISE = 0xff00ff7f;

	public float red, green, blue, alpha;

	/**
	 * @param red Red part. {0.0;255.0}
	 * @param green Green part. {0.0;255.0}
	 * @param blue Blue part. {0.0;255.0}
	 */
	public Color(float red, float green, float blue) {
		this.red = MathHelper.clamp(red, 0F, 255F);
		this.green = MathHelper.clamp(green, 0F, 255F);
		this.blue = MathHelper.clamp(blue, 0F, 255F);
		this.alpha = 1F;
	}

	/**
	 * @param red Red part. {0.0;255.0}
	 * @param green Green part. {0.0;255.0}
	 * @param blue Blue part. {0.0;255.0}
	 * @param alpha Opacity. {0.0;1.0}
	 */
	public Color(float alpha, float red, float green, float blue) {
		this.red = MathHelper.clamp(red, 0F, 255F);
		this.green = MathHelper.clamp(green, 0F, 255F);
		this.blue = MathHelper.clamp(blue, 0F, 255F);
		this.alpha = MathHelper.clamp(alpha, 0F, 1F);
	}

	public Color(int colorInt) {
		this.alpha = (float) (colorInt >> 24 & 255) / 255.0F;
		this.red = (float) (colorInt >> 16 & 255);
		this.green = (float) (colorInt >> 8 & 255);
		this.blue = (float) (colorInt & 255);
	}

	public int toARGBInt() {
		int argb = Math.round(alpha * 255.0F);
		argb = (argb << 8) + Math.round(red);
		argb = (argb << 8) + Math.round(green);
		argb = (argb << 8) + Math.round(blue);
		return argb;
	}

	public String toHexString(boolean prefixed) {
		return (prefixed ? "0x" : "") + Integer.toHexString(toARGBInt());
	}

	@Override
	public String toString() {
		return String.format("argb(%s; %s; %s; %s)", alpha, red, green, blue);
	}

	public static Color fromARGBInt(int colorInt) {
		return new Color(colorInt);
	}
	
}
