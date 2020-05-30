package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import work.lclpnet.mmo.LCLPMMO;

public class FancyButton extends Button{

	public static final ResourceLocation LOCATION_HOVER = new ResourceLocation(LCLPMMO.MODID, "ui.button.hover");
	private int fontColor, hoverFontColor;
	private boolean hover = false;

	public FancyButton(int widthIn, int heightIn, int width, int height, String text, IPressable onPress) {
		this(widthIn, heightIn, width, height, text, onPress, TextFormatting.WHITE.getColor(), TextFormatting.YELLOW.getColor());
	}

	public FancyButton(int widthIn, int heightIn, int width, int height, String text, IPressable onPress, int fontColor, int hoverFontColor) {
		super(widthIn, heightIn, width, height, text, onPress);
		this.fontColor = fontColor;
		this.hoverFontColor = hoverFontColor;
	}

	@Override
	public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
		if(!hover && this.isHovered()) {
			hover = true;
			onHover();
		} else if(hover && !this.isHovered()) {
			hover = false;
			onHoverEnd();
		}

		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fontrenderer = minecraft.fontRenderer;
		int j = getFGColor();
		int color = j | MathHelper.ceil(this.alpha * 255.0F) << 24;
		double scale = 1.5D;
		drawString(fontrenderer, getMessage(), scale, this.x, (int) (this.y + this.height / 2 - fontrenderer.FONT_HEIGHT / scale), color);
	}

	@SuppressWarnings("deprecation")
	private void drawString(FontRenderer fr, String str, double scale, int x, int y, int color) {
		double neg = 1D / scale;
		x *= neg;
		y *= neg;

		GlStateManager.scaled(scale, scale, scale);
		fr.drawStringWithShadow(str, x, y, color);
		GlStateManager.scaled(neg, neg, neg);
	}

	public void onHover() {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(new SoundEvent(LOCATION_HOVER), 1F));
	}

	public void onHoverEnd() {

	}

	public int getFGColor() {
		if (packedFGColor != UNSET_FG_COLOR) return packedFGColor;
		return this.active 
				? (this.isHovered() ? hoverFontColor : fontColor) 
						: 10526880; // White : Light Grey
	}

}
