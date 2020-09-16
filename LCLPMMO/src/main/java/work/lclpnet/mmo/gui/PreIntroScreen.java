package work.lclpnet.mmo.gui;

import java.util.Date;

import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.util.Color;
import work.lclpnet.mmo.util.DateUtil;

@OnlyIn(Dist.CLIENT)
public class PreIntroScreen extends MMOScreen{

	private static final long FADEIN_DELAY = 2000,
			FADEIN_TIME = 2000,
			INTRO_LENGTH = 6000;
	private static final ResourceLocation INTRO_SOUND = new ResourceLocation(LCLPMMO.MODID, DateUtil.isSpecialDay(new Date()) ? "intro_theme_alt" : "intro_theme");
	
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
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		if (this.firstRenderTime == 0L) {
			this.firstRenderTime = System.currentTimeMillis();
			this.firstTitleRenderTime = firstRenderTime + FADEIN_DELAY;
		}
		else if(System.currentTimeMillis() - this.firstRenderTime >= INTRO_LENGTH) {
			minecraft.displayGuiScreen(new MMOMainScreen(true));
			return;
		}

		if(!renderBG) this.fillGradient(0, 0, this.width, this.height, Color.WHITE, Color.WHITE);
		else this.renderBackground();

		float alpha = MathHelper.clamp((System.currentTimeMillis() - firstTitleRenderTime) / (float) FADEIN_TIME, 0F, 1F);

		if(!soundPlayed && System.currentTimeMillis() - firstRenderTime > FADEIN_DELAY) {
			soundPlayed = true;
			minecraft.getSoundHandler().play(new SimpleSound(INTRO_SOUND, SoundCategory.RECORDS, 1F, 1F, false, 0, AttenuationType.NONE, 0F, 0F, 0F, true));
		}
		
		if(alpha > 0.1F) {
			Color color = new Color(alpha, 85, 255, 85);
			drawMultiLineCenteredString(this.font, this.title.getFormattedText(), 1.5D, this.width / 2, this.height / 2, color.toARGBInt());
		}
		super.render(p_render_1_, p_render_2_, p_render_3_);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

}
