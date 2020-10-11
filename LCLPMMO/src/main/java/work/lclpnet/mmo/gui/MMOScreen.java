package work.lclpnet.mmo.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class MMOScreen extends Screen{

	public static final ResourceLocation BACKGROUND_LOCATION_ALT = new ResourceLocation("textures/block/stone_bricks.png");

	protected MMOScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	protected void drawMultiLineCenteredString(MatrixStack mStack, FontRenderer fr, ITextComponent str, float scale, int x, int y, int color) {
		float neg = 1F / scale;
		x *= neg;
		y *= neg;

		for (IReorderingProcessor s : fr.trimStringToWidth(str, this.width)) {
			mStack.push();
			mStack.scale(scale, scale, scale);
			fr.func_238407_a_(mStack, s, (float) (x - fr.func_243245_a(s) / 2.0), y, color);
			mStack.pop();
			y += fr.FONT_HEIGHT * scale;
		}
	}

	protected void drawMultiLineCenteredString(MatrixStack mStack, FontRenderer fr, ITextComponent str, int x, int y) {
		for (IReorderingProcessor s : fr.trimStringToWidth(str, this.width)) {
			fr.func_238407_a_(mStack, s, (float) (x - fr.func_243245_a(s) / 2.0), y, 0xFFFFFF);
			y+=fr.FONT_HEIGHT;
		}
	}

	public void renderBackgroundTexture(ResourceLocation texture) {
		renderBackgroundTexture(0, texture);
	}

	@SuppressWarnings("deprecation")
	public void renderBackgroundTexture(int vOffset, ResourceLocation texture) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		this.minecraft.getTextureManager().bindTexture(texture);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double)this.height, 0.0D).tex(0.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, (double)this.height, 0.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F + (float)vOffset).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos((double)this.width, 0.0D, 0.0D).tex((float)this.width / 32.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0F, (float)vOffset).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent(this, new MatrixStack()));
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
