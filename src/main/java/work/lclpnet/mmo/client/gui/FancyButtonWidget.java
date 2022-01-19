package work.lclpnet.mmo.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class FancyButtonWidget extends ButtonWidget {

    private final int fontColor;
    private final int hoverFontColor;
    private boolean hover = false;
    public float scale = 1.5F;

    public FancyButtonWidget(int widthIn, int heightIn, int width, int height, Text text, PressAction onPress) {
        this(widthIn, heightIn, width, height, text, onPress, Objects.requireNonNull(Formatting.WHITE.getColorValue()),
                Objects.requireNonNull(Formatting.YELLOW.getColorValue()));
    }

    public FancyButtonWidget(int widthIn, int heightIn, int width, int height, Text text, PressAction onPress, int fontColor, int hoverFontColor) {
        super(widthIn, heightIn, width, height, text, onPress);
        this.fontColor = fontColor;
        this.hoverFontColor = hoverFontColor;
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (!hover && this.isHovered()) hover = true;
        else if (hover && !this.isHovered()) hover = false;

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        int color = this.active
                ? (this.isHovered() ? hoverFontColor : fontColor)
                : 10526880;
        // set alpha value
        color = color | MathHelper.ceil(this.alpha * 255.0F) << 24;
        int stringY = (int) (this.y + this.height / 2 - textRenderer.fontHeight / scale);
        drawString(matrices, textRenderer, getMessage(), scale, this.x, stringY, color);
    }

    private void drawString(MatrixStack mStack, TextRenderer fr, Text text, float scale, int x, int y, int color) {
        double neg = 1D / scale;
        x *= neg;
        y *= neg;

        mStack.push();
        mStack.scale(scale, scale, scale);
        fr.drawWithShadow(mStack, text, x, y, color);
        mStack.pop();
    }
}
