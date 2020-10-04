package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MMOScreen extends Screen{

	public static final ResourceLocation BACKGROUND_LOCATION_ALT = new ResourceLocation("textures/block/stone_bricks.png");
	
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

	public void renderBackgroundTexture(ResourceLocation texture) {
		renderBackgroundTexture(0, texture);
	}
	
	@SuppressWarnings("deprecation")
	public void renderBackgroundTexture(int p_renderDirtBackground_1_, ResourceLocation texture) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)p_renderDirtBackground_1_).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this));
	}
	
	public void displayToast(ITextComponent text) {
		displayToast(text, null);
	}
	
	public void displayToast(ITextComponent upperText, ITextComponent lowerText) {
		SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP, 
				upperText, 
				lowerText);
	}

}
