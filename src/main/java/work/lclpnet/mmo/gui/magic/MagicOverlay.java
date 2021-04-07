package work.lclpnet.mmo.gui.magic;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.input.MMOKeybindings;

public class MagicOverlay {

    private static final ResourceLocation MARKER_DEFAULT_LOCATION = new ResourceLocation(LCLPMMO.MODID, "textures/gui/questbook/marker_default.png");

    public static void onEvent(RenderGameOverlayEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        int width = e.getWindow().getScaledWidth();
        int height = e.getWindow().getScaledHeight();
        int overlayWidth = (int) (width * 0.4F);
        MagicOverlay.render(mc, e.getMatrixStack(), width / 2 - overlayWidth, height / 2, overlayWidth);
    }

    private static boolean showing = false;

    public static void render(Minecraft mc, MatrixStack mStack, int x, int y, int width) {
        if(!MMOKeybindings.KEY_MAGIC_OVERLAY.isKeyDown()) {
            if(showing) {
                mc.mouseHelper.grabMouse();
                showing = false;
            }
            return;
        }

        showing = true;
        mc.mouseHelper.ungrabMouse();
        TranslationTextComponent text = new TranslationTextComponent("mmo.hud.magic.title");
        AbstractGui.drawCenteredString(mStack, mc.fontRenderer, text, x, y, 0xffffffff);
    }

    public static boolean isShowing() {
        return showing;
    }
}
