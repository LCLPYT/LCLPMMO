package work.lclpnet.mmo.client.gui;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.asm.mixin.client.SystemToastAccessor;

import javax.annotation.Nullable;
import java.util.List;

public class MMOScreen extends Screen {

    public static final Identifier BACKGROUND_TEXTURE_ALT = new Identifier("textures/block/stone_bricks.png");

    protected MMOScreen(Text titleIn) {
        super(titleIn);
    }

    protected void drawMultiLineCenteredString(MatrixStack matrices, TextRenderer textRenderer, Text text, float scale, int x, int y, int color) {
        float neg = 1F / scale;
        x *= neg;
        y *= neg;

        for (OrderedText line : textRenderer.wrapLines(text, this.width)) {
            matrices.push();
            matrices.scale(scale, scale, scale);
            textRenderer.drawWithShadow(matrices, line, (float) (x - textRenderer.getWidth(line) / 2.0), y, color);
            matrices.pop();
            y += textRenderer.fontHeight * scale;
        }
    }

    protected void drawMultiLineCenteredString(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y) {
        for (OrderedText line : textRenderer.wrapLines(text, this.width)) {
            textRenderer.drawWithShadow(matrices, line, (float) (x - textRenderer.getWidth(line) / 2.0), y, 0xFFFFFF);
            y += textRenderer.fontHeight;
        }
    }

    public void renderBackgroundTexture(Identifier texture) {
        renderBackgroundTexture(0, texture);
    }

    public void renderBackgroundTexture(int vOffset, Identifier texture) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0.0, this.height, 0.0).texture(0.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0.0).texture((float)this.width / 32.0f, (float)this.height / 32.0f + (float)vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(this.width, 0.0, 0.0).texture((float)this.width / 32.0f, vOffset).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0.0, 0.0, 0.0).texture(0.0f, vOffset).color(64, 64, 64, 255).next();
        tessellator.draw();
    }

    public void displayToast(Text text) {
        displayToast(text, null);
    }

    public void displayToast(Text title, Text text) {
        displayToast(this.client, title, text);
    }

    public static void displayToast(MinecraftClient client, Text title, Text text) {
        if (client != null)
            addOrUpdateMultilineToast(client.getToastManager(), SystemToast.Type.WORLD_BACKUP, title, text);
    }

    public static void addOrUpdateMultilineToast(ToastManager toastGui, SystemToast.Type type, Text title, @Nullable Text text) {
        SystemToast systemtoast = toastGui.getToast(SystemToast.class, type);
        if (systemtoast == null) {
            SystemToast toast;
            if (text == null) toast = new SystemToast(type, title, null);
            else {
                final int maxToastWidth = 200;
                final int iconPadding = 18;
                final int maxTextWidth = maxToastWidth - iconPadding;

                TextRenderer textRenderer = toastGui.getClient().textRenderer;
                List<OrderedText> lines = textRenderer.wrapLines(text, maxTextWidth);
                int width = 18 + lines.stream().mapToInt(textRenderer::getWidth).max().orElse(maxTextWidth); // 18 = padding for icon

                toast = new SystemToast(type, title, lines, Math.min(maxToastWidth, width + iconPadding));
            }
            toastGui.add(toast);
        } else {
            setSystemToastTextMultiline(systemtoast, title, text);
        }
    }

    public static void setSystemToastTextMultiline(SystemToast systemtoast, Text title, @Nullable Text text) {
        SystemToastAccessor accessor = ((SystemToastAccessor) systemtoast);
        accessor.setTitle(title);
        accessor.setJustUpdated(true);

        if (text == null) {
            accessor.setLines(ImmutableList.of());
            accessor.setWidth(160);
        } else {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> lines = textRenderer.wrapLines(text, 200);
            int i = Math.max(160, lines.stream().mapToInt(textRenderer::getWidth).max().orElse(160));

            accessor.setLines(lines);
            accessor.setWidth(i <= 160 ? i : i + 30);
        }
    }
}
