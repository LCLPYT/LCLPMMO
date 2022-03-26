package work.lclpnet.mmo.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import work.lclpnet.mmo.client.util.LCLPLauncher;

@Environment(EnvType.CLIENT)
public class UpdateAvailableScreen extends Screen {

    private boolean startFailed = false;

    public UpdateAvailableScreen() {
        super(new LiteralText("Update"));
    }

    @Override
    protected void init() {
        super.init();

         MutableText updateComponent = new TranslatableText("lclpupdater.update")
                .formatted(Formatting.GREEN, Formatting.BOLD);
        addDrawableChild(new ButtonWidget(this.width / 2 - 100, (int) (this.height * 0.45), 200, 20, updateComponent, obj -> LCLPLauncher.startLCLPLauncher().thenRun(() -> {
            if (this.client != null) {
                this.client.stop();
            }
        }).exceptionally(err -> {
            System.err.println("Error while starting LCLPLauncher:");
            err.printStackTrace();
            startFailed = true;
            return null;
        })));

        MutableText cancelComponent = new TranslatableText("lclpupdater.cancel").formatted(Formatting.RED);
        addDrawableChild(new ButtonWidget(this.width / 2 - 100, (int) (this.height * 0.55), 200, 20, cancelComponent, obj -> {
            if (this.client != null) {
                this.client.stop();
            }
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);

        drawMultiLineCenteredString(matrices, textRenderer, new TranslatableText("lclpupdater.title"), 2F, this.width / 2, (int) (this.height * 0.05), 0xfaaf37);
        drawMultiLineCenteredString(matrices, textRenderer, new TranslatableText("lclpupdater.desc", "?"), 1F, this.width / 2, (int) (this.height * 0.2), 0xffffff);

        if (startFailed)
            drawMultiLineCenteredString(matrices, textRenderer, new TranslatableText("lclpupdater.startfailed"), 1F, this.width / 2, (int) (this.height * 0.75), 0xff0000);

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void drawMultiLineCenteredString(MatrixStack mStack, TextRenderer fr, Text str, float scale, int x, int y, int color) {
        float neg = 1F / scale;
        x *= neg;
        y *= neg;

        for (OrderedText s : fr.wrapLines(str, this.width)) {
            mStack.push();
            mStack.scale(scale, scale, scale);
            fr.draw(mStack, s, (float) (x - fr.getWidth(s) / 2.0), y, color);
            mStack.pop();
            y += fr.fontHeight * scale;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
