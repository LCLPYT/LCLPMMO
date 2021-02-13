package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.audio.MMOSoundEvents;

@OnlyIn(Dist.CLIENT)
public class FancyButton extends Button{

	private int fontColor, hoverFontColor;
	private boolean hover = false;
	public float scale = 1.5F;

	public FancyButton(int widthIn, int heightIn, int width, int height, ITextComponent text, IPressable onPress) {
		this(widthIn, heightIn, width, height, text, onPress, TextFormatting.WHITE.getColor(), TextFormatting.YELLOW.getColor());
	}

	public FancyButton(int widthIn, int heightIn, int width, int height, ITextComponent text, IPressable onPress, int fontColor, int hoverFontColor) {
		super(widthIn, heightIn, width, height, text, onPress);
		this.fontColor = fontColor;
		this.hoverFontColor = hoverFontColor;
	}

	@Override
	public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
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
		drawString(mStack, fontrenderer, getMessage(), scale, this.x, (int) (this.y + this.height / 2 - fontrenderer.FONT_HEIGHT / scale), color);
	}

	private void drawString(MatrixStack mStack, FontRenderer fr, ITextComponent str, float scale, int x, int y, int color) {
		double neg = 1D / scale;
		x *= neg;
		y *= neg;

		mStack.push();
		mStack.scale(scale, scale, scale);
		fr.func_243246_a(mStack, str, x, y, color);
		mStack.pop();
	}

	public void onHover() {
		Minecraft.getInstance().getSoundHandler().play(SimpleSound.master(MMOSoundEvents.UI_BUTTON_HOVER, 1F));
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
