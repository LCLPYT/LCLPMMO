package work.lclpnet.mmo.gui.main;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.gui.FancyButton;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.gui.character.CharacterChooserScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.util.LCLPNetwork;

@OnlyIn(Dist.CLIENT)
public class MMOMainScreen extends MMOScreen{

	public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/panorama"));
	private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/panorama_overlay.png"),
			MINECRAFT_TITLE_TEXTURES = new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/title.png"),
			ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png"),
			THEME_MUSIC = new ResourceLocation(LCLPMMO.MODID, "music.ls5");
	private final RenderSkybox panorama = new RenderSkybox(PANORAMA_RESOURCES);
	private boolean showFadeInAnimation;
	private long firstRenderTime = 0L;
	private List<MMOButtonInfo> menuButtons = new ArrayList<>();
	private ClientPlayerEntity player;

	public MMOMainScreen(boolean fade) {
		super(new StringTextComponent("Main menu"));
		this.showFadeInAnimation = fade;

		setupButtons();
	}

	@SuppressWarnings("unchecked")
	private void setupEntity() {
		if(player != null && Minecraft.getInstance().getConnection() != null) return;
		ClientPlayNetHandler netHandler = new FakeClientPlayNetHandler(minecraft);
		ClientWorld world = new FakeWorld(netHandler, new WorldSettings(0, GameType.NOT_SET, true, false, WorldType.DEFAULT));
		player = new ClientPlayerEntity(minecraft, world, netHandler, null, null);

		Field f;
		DataParameter<Byte> PLAYER_MODEL_FLAG = null;
		try {
			f = ObfuscationReflectionHelper.findField(PlayerEntity.class, "field_184827_bp");
		} catch (UnableToFindFieldException e) {
			e.printStackTrace();
			f = null;
		}

		if(f != null) {
			try {
				f.setAccessible(true);
				Object o = f.get(null);
				PLAYER_MODEL_FLAG = (DataParameter<Byte>) o;
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}

			int modelParts = 0;
			for (PlayerModelPart part : minecraft.gameSettings.getModelParts())
				modelParts |= part.getPartMask();

			if(PLAYER_MODEL_FLAG != null) player.getDataManager().set(PLAYER_MODEL_FLAG, (byte) modelParts);
		}

		minecraft.player = player;
		minecraft.getRenderManager().cacheActiveRenderInfo(world, minecraft.gameRenderer.getActiveRenderInfo(), player);
	}

	private void setupButtons() {
		this.menuButtons.add(new MMOButtonInfo(I18n.format("menu.singleplayer"), b -> this.minecraft.displayGuiScreen(new WorldSelectionScreen(this))));
		this.menuButtons.add(new MMOButtonInfo(I18n.format("menu.multiplayer"), b -> {
			if (this.minecraft.gameSettings.field_230152_Z_) this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
			else this.minecraft.displayGuiScreen(new MultiplayerWarningScreen(this));
		}));
		this.menuButtons.add(new MMOButtonInfo(I18n.format("fml.menu.mods"), b -> this.minecraft.displayGuiScreen(new ModListScreen(this))));
		this.menuButtons.add(new MMOButtonInfo(I18n.format("menu.options"), b -> this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings))));
		this.menuButtons.add(new MMOButtonInfo(I18n.format("mmo.menu.btn_create_character"), b -> CharacterChooserScreen.updateContentAndShow(this.minecraft, this)));
	}

	@Override
	protected void init() {
		setupEntity();

		int x = (int) (this.width / 12.8), y = (int) (this.height / 2.8);
		int width = this.width / 2 - 100, height = this.height / 18;
		int marginTop = this.height / 36;

		for(MMOButtonInfo b : menuButtons) {
			this.addButton(new FancyButton(x, y, width, height, b.text, b.onClick, b.color, b.hoverColor));
			y += height + marginTop;
		}

		int quitY = (int) (this.height * 0.9);
		int imgBtnY = quitY - (int) (40 * (this.height / 360D));

		this.addButton(new ImageButton(x, imgBtnY, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, b -> {
			this.minecraft.displayGuiScreen(new LanguageScreen(this, this.minecraft.gameSettings, this.minecraft.getLanguageManager()));
		}, I18n.format("narrator.button.language")));

		this.addButton(new ImageButton(x + 25, imgBtnY, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, b -> {
			this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
		}, I18n.format("narrator.button.accessibility")));

		FancyButton quitButton = new FancyButton(x, quitY, 
				width, height, 
				I18n.format("menu.quit"), 
				b -> this.minecraft.shutdown(), 
				0xFFFF7070, TextFormatting.RED.getColor());
		this.addButton(quitButton);

		boolean loggedIn = LCLPNetwork.isLoggedIn();
		String logoutText = loggedIn ? I18n.format("mmo.menu.logout") : I18n.format("mmo.menu.login");
		FancyButton logoutBtn = new FancyButton(
				this.width - this.font.getStringWidth(logoutText) - 10,
				this.height - this.font.FONT_HEIGHT - 5,
				this.font.getStringWidth(logoutText) + 5,
				this.font.FONT_HEIGHT + 5,
				logoutText,
				b -> {
					if(!loggedIn) this.minecraft.displayGuiScreen(new LoginScreen());
					else this.minecraft.displayGuiScreen(new ConfirmScreen(yes -> {
						if(yes) {
							LCLPNetwork.logout();
							this.minecraft.displayGuiScreen(new LoginScreen());
						} else {
							this.minecraft.displayGuiScreen(MMOMainScreen.this);
						}
					}, new TranslationTextComponent("mmo.menu.confirm_logout"), new TranslationTextComponent("mmo.menu.confirm_logout_desc")));
				},
				0xFFFF7070, TextFormatting.RED.getColor());
		logoutBtn.scale = 1D;
		this.addButton(logoutBtn);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
			this.firstRenderTime = Util.milliTime();
			onStart();
		}

		float alphaRaw = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
		this.panorama.render(partialTicks, MathHelper.clamp(alphaRaw, 0.0F, 1.0F));

		this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(alphaRaw, 0.0F, 1.0F)) : 1.0F);
		blit(0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

		float alpha = this.showFadeInAnimation ? MathHelper.clamp(alphaRaw - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(alpha * 255.0F) << 24;
		if ((l & -67108864) != 0) { //Prevent "flicker" when fading
			this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			int padding = (int) (this.width / 21.3);
			double scale = 1D / (360D / this.height),
					neg = 1D / scale;
			GlStateManager.scaled(scale, scale, scale);
			this.blit(padding, padding, 0, 0, 255, 84);
			GlStateManager.scaled(neg, neg, neg);

			this.drawPlayerModel(partialTicks, mouseX, mouseY, alpha);

			for(Widget widget : this.buttons) {
				widget.setAlpha(alpha);
			}

			super.render(mouseX, mouseY, partialTicks);
		}
	}

	protected void drawPlayerModel(float partialTicks, int mouseX, int mouseY, float alpha) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int x = (int) (this.width * 0.8), y = (int) (this.height * 0.8);
		int scale = (int) (100F * (this.height / 360F));

		mouseY = MathHelper.clamp(mouseY, (int) (this.height * 0.6F), (int) (this.height * 0.75F));

		RenderSystem.translatef(0F, 0F, 60F);
		InventoryScreen.drawEntityOnScreen(x, y, scale, (float)(x) - mouseX, (float)(y - 50) - mouseY, player);
		RenderSystem.translated(0F, 0F, -60F);
	}

	private void onStart() {
		minecraft.getSoundHandler().play(SimpleSound.music(new SoundEvent(THEME_MUSIC)));
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public class MMOButtonInfo {

		public String text;
		public IPressable onClick;
		public int color, hoverColor;

		public MMOButtonInfo(String text, IPressable onClick) {
			this(text, onClick, TextFormatting.WHITE.getColor(), TextFormatting.YELLOW.getColor());
		}

		public MMOButtonInfo(String text, IPressable onClick, int color, int hoverColor) {
			this.text = text;
			this.onClick = onClick;
			this.color = color;
			this.hoverColor = hoverColor;
		}

	}

}
