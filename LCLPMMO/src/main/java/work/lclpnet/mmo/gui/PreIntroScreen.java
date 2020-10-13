package work.lclpnet.mmo.gui;

import java.util.Date;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.util.Color;
import work.lclpnet.mmo.util.DateUtil;
import work.lclpnet.mmo.util.MMONames;

@OnlyIn(Dist.CLIENT)
public class PreIntroScreen extends MMOScreen{

	private static final long FADEIN_DELAY = 2000,
			FADEIN_TIME = 2000,
			INTRO_LENGTH = 6000;
	private static final int bgColor = ColorHelper.PackedColor.packColor(255, 239, 50, 61);

	private long firstRenderTime = 0L, firstTitleRenderTime = 0L;
	private boolean soundPlayed = false;
	public boolean renderBG = false;

	public PreIntroScreen() {
		super(new TranslationTextComponent("lclpnetwork.presents"));
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		if (this.firstRenderTime == 0L) {
			this.firstRenderTime = System.currentTimeMillis();
			this.firstTitleRenderTime = firstRenderTime + FADEIN_DELAY;
		}
		else if(System.currentTimeMillis() - this.firstRenderTime >= INTRO_LENGTH) {
			minecraft.displayGuiScreen(new MMOMainScreen(true));
			return;
		}

		if(!renderBG) fill(mStack, 0, 0, this.width, this.height, bgColor);
		else this.renderBackground(mStack);

		float alpha = MathHelper.clamp((System.currentTimeMillis() - firstTitleRenderTime) / (float) FADEIN_TIME, 0F, 1F);

		if(!soundPlayed && System.currentTimeMillis() - firstRenderTime > FADEIN_DELAY) {
			soundPlayed = true;
			ResourceLocation rl = new ResourceLocation(DateUtil.isSpecialDay(new Date()) ? 
					MMONames.Sound.INTRO_THEME_ALT : MMONames.Sound.INTRO_THEME);
			minecraft.getSoundHandler().play(new SimpleSound(rl, SoundCategory.RECORDS, 1F, 1F, false, 0, AttenuationType.NONE, 0F, 0F, 0F, true));
		}

		if(alpha > 0.1F) {
			Color color = new Color(alpha, 85, 255, 85);
			drawMultiLineCenteredString(mStack, this.font, this.title, 1.5F, this.width / 2, this.height / 2, color.toARGBInt());
		}
		super.render(mStack, mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

}
