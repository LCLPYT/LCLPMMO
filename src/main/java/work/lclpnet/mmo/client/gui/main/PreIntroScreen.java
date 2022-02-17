package work.lclpnet.mmo.client.gui.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.util.MMOUtils;

@Environment(EnvType.CLIENT)
public class PreIntroScreen extends MMOScreen {

    private static final long FADEIN_DELAY = 2000,
            FADEIN_TIME = 2000,
            INTRO_LENGTH = 6000;
    private static final int bgColor = BackgroundHelper.ColorMixer.getArgb(255, 239, 50, 61);

    private long firstRenderTime = 0L, firstTitleRenderTime = 0L;
    private boolean soundPlayed = false;
    public boolean renderBG = false;

    public PreIntroScreen() {
        super(new TranslatableText("lclpnetwork.presents"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (this.firstRenderTime == 0L) {
            this.firstRenderTime = System.currentTimeMillis();
            this.firstTitleRenderTime = firstRenderTime + FADEIN_DELAY;
        } else if (System.currentTimeMillis() - this.firstRenderTime >= INTRO_LENGTH) {
            if (client != null) client.openScreen(new MMOTitleScreen(true));
            return;
        }

        if (!renderBG) fill(matrices, 0, 0, this.width, this.height, bgColor);
        else this.renderBackground(matrices);

        float alpha = MathHelper.clamp((System.currentTimeMillis() - firstTitleRenderTime) / (float) FADEIN_TIME, 0F, 1F);

        if (!soundPlayed && System.currentTimeMillis() - firstRenderTime > FADEIN_DELAY) {
            soundPlayed = true;
            Identifier introSound = LCLPMMO.identifier(MMOUtils.isSpecialDate() ? "intro_theme_alt" : "intro_theme");
            if (client != null) {
                client.getSoundManager().play(new PositionedSoundInstance(introSound, SoundCategory.RECORDS, 0.5F, 1F, false, 0, SoundInstance.AttenuationType.NONE, 0F, 0F, 0F, true));
            }
        }

        if (alpha > 0.1F) {
            Color color = new Color(alpha, 85, 255, 85);
            drawMultiLineCenteredString(matrices, this.textRenderer, this.title, 1.5F, this.width / 2, this.height / 2, color.toARGBInt());
        }
        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
