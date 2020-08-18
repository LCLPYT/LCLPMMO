package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

public class MMOScreen extends Screen{

	protected MMOScreen(ITextComponent titleIn) {
		super(titleIn);
	}
	
	@SuppressWarnings("deprecation")
	protected void drawMultiLineCenteredString(FontRenderer fr, String str, double scale, int x, int y, int color) {
		double neg = 1D / scale;
		x *= neg;
		y *= neg;

		for (String s : fr.listFormattedStringToWidth(str, this.width)) {
			GlStateManager.scaled(scale, scale, scale);
			fr.drawStringWithShadow(s, (float) (x - fr.getStringWidth(s) / 2.0), y, color);
			GlStateManager.scaled(neg, neg, neg);
			y += fr.FONT_HEIGHT * scale;
		}
	}

}
