package work.lclpnet.mmo.client.gui.main;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.asm.mixin.client.TexturedButtonWidgetAccessor;

public class CustomImageButton extends TexturedButtonWidget {

    private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");
    protected final int uWidth, vHeight;
    protected final int padding;

    public CustomImageButton(int x, int y, int width, int height, int u, int v, int hoveredVOffset, int uWidth, int vHeight, Identifier texture, int textureWidth, int textureHeight, int padding, PressAction pressAction, TooltipSupplier tooltipSupplier, Text text) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, tooltipSupplier, text);
        this.uWidth = uWidth;
        this.vHeight = vHeight;
        this.padding = padding;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        TexturedButtonWidgetAccessor accessor = (TexturedButtonWidgetAccessor) this;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        byte texture = (byte) (isHovered() ? 2 : 1);

        /* Draw background */
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int textureY = 46 + texture * 20;
        int halfWidth = width / 2;
        int secondHalfWidth = width - halfWidth;
        int halfHeight = height / 2;
        int secondHalfHeight = height - halfHeight;

        drawTexture(matrices, x, y, 0, textureY, halfWidth, halfHeight);
        drawTexture(matrices, x, y + halfHeight, 0, textureY + 20 - secondHalfHeight, halfWidth, secondHalfHeight);
        drawTexture(matrices, x + halfWidth, y, 200 - secondHalfWidth, textureY, secondHalfWidth, halfHeight);
        drawTexture(matrices, x + halfWidth, y + halfHeight, 200 - secondHalfWidth, textureY + 20 - secondHalfHeight, secondHalfWidth, secondHalfHeight);

        /* Draw texture */
        RenderSystem.setShaderTexture(0, accessor.getTexture());
        int v = accessor.getV();
        if (this.isHovered())
            v += accessor.getHoveredVOffset();

        RenderSystem.enableDepthTest();
        final int paddingTwice = 2 * padding;
        drawTexture(matrices, x + padding, y + padding, this.width - paddingTwice, this.height - paddingTwice, (float) accessor.getU(), (float) v, uWidth, vHeight, accessor.getTextureWidth(), accessor.getTextureHeight());
        if (this.isHovered())
            this.renderTooltip(matrices, mouseX, mouseY);
    }
}
