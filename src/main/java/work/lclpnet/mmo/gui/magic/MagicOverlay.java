package work.lclpnet.mmo.gui.magic;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.input.MMOKeybindings;
import work.lclpnet.mmo.util.Color;

public class MagicOverlay {

    private static final ResourceLocation MARKER_DEFAULT_LOCATION = new ResourceLocation(LCLPMMO.MODID, "textures/gui/questbook/marker_default.png");

    public static void onEvent(RenderGameOverlayEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        MagicOverlay.doRender(mc, e.getMatrixStack(), e.getWindow().getScaledWidth(), e.getWindow().getScaledHeight());
    }

    private static boolean showing = false;

    public static boolean isShowing() {
        return showing;
    }

    public static void doRender(Minecraft mc, MatrixStack mStack, int width, int height) {
        if(!MMOKeybindings.KEY_MAGIC_OVERLAY.isKeyDown()) {
            if(showing) {
                mc.mouseHelper.grabMouse();
                showing = false;
            }
            return;
        }

        showing = true;
        mc.mouseHelper.ungrabMouse();
        render(mc, mStack, width, height);
    }

    private static void render(Minecraft mc, MatrixStack mStack, int screenWidth, int screenHeight) {
        fillCircle(screenWidth / 2D, screenHeight / 2D, 50, 64, Color.RED);
    }

    /*public static void fillCircle(double x, double y, int radius, int sides) {
        double TWICE_PI = Math.PI * 2D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x, y, 0).endVertex();

        for(int i = 0; i <= sides ;i++)
        {
            double angle = (TWICE_PI * i / sides) + Math.toRadians(180);
            bufferbuilder.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
        }
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }*/

    public static void fillCircle(double x, double y, int radius, int sides, int color) {
        float alpha = (float)(color >> 24 & 255) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;

        double TWICE_PI = Math.PI * 2D;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0).color(red, green, blue, alpha).endVertex();

        for(int i = 0; i <= sides; i++) {
            double angle = (TWICE_PI * i / sides) + Math.toRadians(180);
            bufferbuilder.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).color(red, green, blue, alpha).endVertex();
        }

        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

}
