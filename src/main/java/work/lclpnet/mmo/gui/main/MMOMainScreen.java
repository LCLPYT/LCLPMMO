package work.lclpnet.mmo.gui.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.IPressable;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.ClientWorldInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.screen.ModListScreen;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.gui.FancyButton;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.gui.character.CharacterChooserScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MMOMainScreen extends MMOScreen{

	public static final RenderSkyboxCube PANORAMA_RESOURCES = new RenderSkyboxCube(new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/panorama"));
	private static final ResourceLocation PANORAMA_OVERLAY_TEXTURES = new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/panorama_overlay.png"),
			MINECRAFT_TITLE_TEXTURES = new ResourceLocation(LCLPMMO.MODID, "textures/gui/main/title.png"),
			ACCESSIBILITY_TEXTURES = new ResourceLocation("textures/gui/accessibility.png");
	private final RenderSkybox panorama = new RenderSkybox(PANORAMA_RESOURCES);
	private final boolean showFadeInAnimation;
	private long firstRenderTime = 0L;
	private final List<MMOButtonInfo> menuButtons = new ArrayList<>();
	private ClientPlayerEntity player;

	public MMOMainScreen(boolean fade) {
		super(new StringTextComponent("Main menu"));
		this.showFadeInAnimation = fade;

		setupButtons();
	}

	private void setupEntity() {
		if(player != null && Minecraft.getInstance().getConnection() != null) return;
		ClientPlayNetHandler netHandler = new FakeClientPlayNetHandler(minecraft);
		ClientWorld world = new FakeWorld(netHandler, new ClientWorldInfo(Difficulty.NORMAL, false, false));
		player = new ClientPlayerEntity(minecraft, world, netHandler, null, null, false, false);
		IMMOUser.initMyPlayer(player);

		DataParameter<Byte> PLAYER_MODEL_FLAG = PlayerEntity.PLAYER_MODEL_FLAG;
		int modelParts = 0;
		for (PlayerModelPart part : minecraft.gameSettings.getModelParts())
			modelParts |= part.getPartMask();

		if(PLAYER_MODEL_FLAG != null) player.getDataManager().set(PLAYER_MODEL_FLAG, (byte) modelParts);

		minecraft.player = player;
		minecraft.getRenderManager().cacheActiveRenderInfo(world, minecraft.gameRenderer.getActiveRenderInfo(), player);
	}

	private void setupButtons() {
		this.menuButtons.add(new MMOButtonInfo(new TranslationTextComponent("menu.singleplayer"), b -> this.minecraft.displayGuiScreen(new WorldSelectionScreen(this))));
		this.menuButtons.add(new MMOButtonInfo(new TranslationTextComponent("menu.multiplayer"), b -> {
			if (this.minecraft.gameSettings.skipMultiplayerWarning) this.minecraft.displayGuiScreen(new MultiplayerScreen(this));
			else this.minecraft.displayGuiScreen(new MultiplayerWarningScreen(this));
		}));
		this.menuButtons.add(new MMOButtonInfo(new TranslationTextComponent("fml.menu.mods"), b -> this.minecraft.displayGuiScreen(new ModListScreen(this))));
		this.menuButtons.add(new MMOButtonInfo(new TranslationTextComponent("menu.options"), b -> this.minecraft.displayGuiScreen(new OptionsScreen(this, this.minecraft.gameSettings))));
		this.menuButtons.add(new MMOButtonInfo(new TranslationTextComponent("mmo.menu.btn_create_character"), b -> CharacterChooserScreen.updateContentAndShow(this.minecraft, this)));
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
		}, new TranslationTextComponent("narrator.button.language")));

		this.addButton(new ImageButton(x + 25, imgBtnY, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURES, 32, 64, b -> {
			this.minecraft.displayGuiScreen(new AccessibilityScreen(this, this.minecraft.gameSettings));
		}, new TranslationTextComponent("narrator.button.accessibility")));

		FancyButton quitButton = new FancyButton(x, quitY, 
				width, height, 
				new TranslationTextComponent("menu.quit"), 
				b -> this.minecraft.shutdown(), 
				0xFFFF7070, TextFormatting.RED.getColor());
		this.addButton(quitButton);

		boolean loggedIn = LCLPNetwork.isLoggedIn();
		ITextComponent logoutText = new TranslationTextComponent(loggedIn ? "mmo.menu.logout" : "mmo.menu.login");
		FancyButton logoutBtn = new FancyButton(
				this.width - this.font.getStringPropertyWidth(logoutText) - 10, 
				this.height - this.font.FONT_HEIGHT - 5,
				this.font.getStringPropertyWidth(logoutText) + 5, 
				this.font.FONT_HEIGHT + 5,
				logoutText,
				b -> {
					if(!loggedIn) {
						LCLPNetwork.setup(() -> {
							if(!LCLPNetwork.isOnline()) {
								displayToast(new TranslationTextComponent("mmo.no_internet"));
							} else {
								if(LCLPNetwork.isLoggedIn()) {
									displayToast(new TranslationTextComponent("mmo.menu.login.login_successful"));
									this.minecraft.displayGuiScreen(new MMOMainScreen(false));
								} else {
									this.minecraft.displayGuiScreen(new LoginScreen());									
								}
							}
						});
					}
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
		logoutBtn.scale = 1F;
		this.addButton(logoutBtn);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
		if (this.firstRenderTime == 0L && this.showFadeInAnimation) {
			this.firstRenderTime = Util.milliTime();
			this.minecraft.getMusicTicker().timeUntilNextMusic = 0;
		}

		float alphaRaw = this.showFadeInAnimation ? (float)(Util.milliTime() - this.firstRenderTime) / 1000.0F : 1.0F;
		this.panorama.render(partialTicks, MathHelper.clamp(alphaRaw, 0.0F, 1.0F));

		this.minecraft.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float)MathHelper.ceil(MathHelper.clamp(alphaRaw, 0.0F, 1.0F)) : 1.0F);
		blit(mStack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

		float alpha = this.showFadeInAnimation ? MathHelper.clamp(alphaRaw - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(alpha * 255.0F) << 24;
		if ((l & -67108864) != 0) { //Prevent "flicker" when fading
			this.minecraft.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
			int padding = (int) (this.width / 21.3);
			float scale = 1F / (360F / this.height);
			mStack.push();
			mStack.scale(scale, scale, scale);
			this.blit(mStack, padding, padding, 0, 0, 255, 84);
			mStack.pop();

			this.drawPlayerModel(partialTicks, mouseX, mouseY, alpha);

			for(Widget widget : this.buttons) {
				widget.setAlpha(alpha);
			}

			super.render(mStack, mouseX, mouseY, partialTicks);
		}
	}

	@SuppressWarnings("deprecation")
	protected void drawPlayerModel(float partialTicks, int mouseX, int mouseY, float alpha) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int x = (int) (this.width * 0.8), y = (int) (this.height * 0.8);
		int scale = (int) (100F * (this.height / 360F));

		mouseY = MathHelper.clamp(mouseY, (int) (this.height * 0.6F), (int) (this.height * 0.75F));

		RenderSystem.translatef(0F, 0F, 60F);
		InventoryScreen.drawEntityOnScreen(x, y, scale, (float)(x) - mouseX, (float)(y - 50) - mouseY, player);
		RenderSystem.translated(0F, 0F, -60F);
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public class MMOButtonInfo {

		public ITextComponent text;
		public IPressable onClick;
		public int color, hoverColor;

		public MMOButtonInfo(ITextComponent text, IPressable onClick) {
			this(text, onClick, TextFormatting.WHITE.getColor(), TextFormatting.YELLOW.getColor());
		}

		public MMOButtonInfo(ITextComponent text, IPressable onClick, int color, int hoverColor) {
			this.text = text;
			this.onClick = onClick;
			this.color = color;
			this.hoverColor = hoverColor;
		}

	}

}
