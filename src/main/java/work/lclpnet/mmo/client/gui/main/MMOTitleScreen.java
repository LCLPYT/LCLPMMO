package work.lclpnet.mmo.client.gui.main;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.asm.mixin.client.GameOptionsAccessor;
import work.lclpnet.mmo.asm.mixin.common.PlayerEntityAccessor;
import work.lclpnet.mmo.client.MMOClient;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.client.gui.character.CharacterChooserScreen;
import work.lclpnet.mmo.client.gui.login.LoginScreen;
import work.lclpnet.mmo.client.gui.widget.FancyButtonWidget;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class MMOTitleScreen extends MMOScreen {

    public static final CubeMapRenderer PANORAMA_RESOURCES = new CubeMapRenderer(LCLPMMO.identifier("textures/gui/main/panorama"));
    private static final Identifier PANORAMA_OVERLAY = LCLPMMO.identifier("textures/gui/main/panorama_overlay.png"),
            MINECRAFT_TITLE_TEXTURE = LCLPMMO.identifier("textures/gui/main/title.png"),
            ACCESSIBILITY_TEXTURES = new Identifier("textures/gui/accessibility.png");
    private static final AtomicBoolean initialTitleScreenShown = new AtomicBoolean(false);

    private final RotatingCubeMapRenderer panorama = new RotatingCubeMapRenderer(PANORAMA_RESOURCES);
    private final boolean doBackgroundFade;
    private long backgroundFadeStart = 0L;
    private final List<MMOButtonInfo> menuButtons = new ArrayList<>();
    private ClientPlayerEntity player;

    public MMOTitleScreen(boolean fade) {
        super(new LiteralText("Main menu"));
        this.doBackgroundFade = fade;

        setupButtons();
    }

    public static void setInitialTitleScreenShown() {
        initialTitleScreenShown.set(true);
    }

    private void setupEntity() {
        if (client == null || (player != null && client.getNetworkHandler() != null && !hasGameProfileChanged()) || !initialTitleScreenShown.get())
            return;

        ClientPlayNetworkHandler netHandler = new FakeClientPlayNetworkHandler(client);
        ClientWorld world = new FakeClientWorld(netHandler, new ClientWorld.Properties(Difficulty.NORMAL, false, false));
        player = new ClientPlayerEntity(client, world, netHandler, null, null, false, false);
        MMOClient.initializeMyPlayer(player);

        TrackedData<Byte> PLAYER_MODEL_FLAG = PlayerEntityAccessor.getPlayerModelFlag();
        int modelParts = 0;
        for (PlayerModelPart part : ((GameOptionsAccessor) client.options).getEnabledPlayerModelParts())
            modelParts |= part.getBitFlag();

        if (PLAYER_MODEL_FLAG != null)
            player.getDataTracker().set(PLAYER_MODEL_FLAG, (byte) modelParts);

        client.player = player;
        client.getEntityRenderDispatcher().configure(world, client.gameRenderer.getCamera(), player);
    }

    private boolean hasGameProfileChanged() {
        Session session = Objects.requireNonNull(client).getSession();
        if (session == null) return false;

        GameProfile dummyPlayerProfile = player.getGameProfile();
        if (dummyPlayerProfile == null) return false;

        return !dummyPlayerProfile.equals(session.getProfile());
    }

    @Override
    protected void init() {
        if (this.client == null) return;

        setupEntity();

        final int btnWidth = width / 2 - 100, btnHeight = height / 18;
        final int btnVSpacing = (int) (btnHeight * 0.6F);

        int currentBtnX = (int) (this.width / 12.8), currentBtnY = (int) (this.height / 2.8);

        for (MMOButtonInfo b : menuButtons) {
            FancyButtonWidget btn = new FancyButtonWidget(currentBtnX, currentBtnY, btnWidth, btnHeight, b.text, b.onClick, b.color, b.hoverColor);
            btn.scale = 1.5F;
            this.addDrawableChild(btn);
            currentBtnY += btnHeight + btnVSpacing;
        }

        final int quitY = (int) (this.height * 0.9);

        final int imgBtnDim = 20;
        final int imgBtnY = quitY - btnVSpacing - imgBtnDim;

        this.addDrawableChild(new TexturedButtonWidget(currentBtnX,
                imgBtnY,
                imgBtnDim,
                imgBtnDim,
                0,
                106,
                20,
                ButtonWidget.WIDGETS_TEXTURE,
                256,
                256,
                b -> this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())),
                new TranslatableText("narrator.button.language")));

        this.addDrawableChild(new TexturedButtonWidget(currentBtnX + 25,
                imgBtnY,
                imgBtnDim,
                imgBtnDim,
                0,
                0,
                20,
                ACCESSIBILITY_TEXTURES,
                32,
                64,
                b -> this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options)),
                new TranslatableText("narrator.button.accessibility")));

        CustomImageButton replayModViewer = ReplayModIntegration.getWidget(currentBtnX + 50, imgBtnY, imgBtnDim);
        if (replayModViewer != null)
            this.addDrawableChild(replayModViewer);

        Integer hoverColor = Formatting.RED.getColorValue();
        FancyButtonWidget quitButton = new FancyButtonWidget(currentBtnX,
                quitY,
                btnWidth,
                btnHeight,
                new TranslatableText("menu.quit"),
                b -> this.client.scheduleStop(),
                0xFFFF7070,
                hoverColor != null ? hoverColor : 0xFFFF7070);
        quitButton.scale = 1.5F;

        this.addDrawableChild(quitButton);

        final boolean loggedIn = LCLPNetworkSession.isLoggedIn();
        Text logoutText = new TranslatableText(loggedIn ? "mmo.menu.logout" : "mmo.menu.login");
        FancyButtonWidget logoutBtn = new FancyButtonWidget(
                this.width - this.textRenderer.getWidth(logoutText) - 10,
                this.height - this.textRenderer.fontHeight - 5,
                this.textRenderer.getWidth(logoutText) + 5,
                this.textRenderer.fontHeight + 5,
                logoutText,
                b -> {
                    if (!loggedIn) doLogin();
                    else doLogout();
                },
                0xFFFF7070,
                hoverColor != null ? hoverColor : 0xFFFF7070);
        logoutBtn.scale = 1F;
        this.addDrawableChild(logoutBtn);
    }

    private void setupButtons() {
        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.singleplayer"), b -> {
            if (this.client != null)
                this.client.setScreen(new SelectWorldScreen(this));
        }));

        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.multiplayer"), b -> {
            if (this.client == null) return;

            if (!LCLPNetworkSession.isLoggedIn()) {
                displayToast(new TranslatableText("mmo.menu.error"), new TranslatableText("mmo.menu.login_first"));
                client.setScreen(new LoginScreen());
                return;
            }

            this.client.setScreen(new MultiplayerScreen(this));
        }));

        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.options"), b -> {
            if (this.client != null)
                this.client.setScreen(new OptionsScreen(this, this.client.options));
        }));

        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("mmo.menu.btn_create_character"),
                b -> CharacterChooserScreen.updateContentAndShow(this.client, this)));
    }

    protected void doLogin() {
        Objects.requireNonNull(client);

        // TODO make stateful and add a loading indicator

        LCLPNetworkSession.startup().thenRun(() -> {
            if (LCLPNetworkSession.isLoggedIn()) {
                displayToast(new TranslatableText("mmo.menu.login.login_successful"));
                this.client.setScreen(new MMOTitleScreen(false));
            } else {
                this.client.setScreen(new LoginScreen());
            }
        }).exceptionally(err -> {
            this.client.setScreen(new LoginScreen());
            err.printStackTrace();
            return null;
        });
    }

    protected void doLogout() {
        if (this.client == null) throw new AssertionError();

        this.client.setScreen(new ConfirmScreen(accepted -> {
            if (accepted) {
                MMOClient.logout().exceptionally(err -> {
                    err.printStackTrace();
                    return null;
                });
                this.client.setScreen(new LoginScreen());
            } else {
                this.client.setScreen(MMOTitleScreen.this);
            }
        }, new TranslatableText("mmo.menu.confirm_logout"), new TranslatableText("mmo.menu.confirm_logout_desc")));
    }

    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (this.client == null) throw new AssertionError();

        if (this.backgroundFadeStart == 0L && this.doBackgroundFade)
            this.backgroundFadeStart = Util.getMeasuringTimeMs();

        // Overlay
        float alphaRaw = this.doBackgroundFade ? (float) (Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
        this.panorama.render(partialTicks, MathHelper.clamp(alphaRaw, 0.0F, 1.0F));

        // Panorama Overlay
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(alphaRaw, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.drawTexture(matrices, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);

        float alpha = this.doBackgroundFade ? MathHelper.clamp(alphaRaw - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(alpha * 255.0F) << 24;
        if ((l & -67108864) == 0) return;  // Prevent "flicker" when fading

        final float scale = 1F / (360F / this.height);

        // Logo
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        final int logoDim = (int) (this.width * 0.05);
        matrices.push();
        matrices.scale(scale, scale, scale);
        drawTexture(matrices, (int) (logoDim / scale), (int) (logoDim / scale), 0, 0, 255, 84);
        matrices.pop();

        // Player Model
        this.drawPlayerModel(mouseX, mouseY, alpha);

        // Button Opacity
        for (Element element : this.children())
            if (element instanceof ClickableWidget)
                ((ClickableWidget) element).setAlpha(alpha);

        // super
        super.render(matrices, mouseX, mouseY, partialTicks);
    }

    protected void drawPlayerModel(int mouseX, int mouseY, float alpha) {
        if (client == null) throw new AssertionError();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        int x = (int) (this.width * 0.8), y = (int) (this.height * 0.8);
        int scale = (int) (100F * (this.height / 360F));

        mouseY = MathHelper.clamp(mouseY, (int) (this.height * 0.6F), (int) (this.height * 0.75F));
//        mouseY += playerMouseCorrection;

        final float zOffset = 60F;

        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.translate(0F, 0F, zOffset);
        InventoryScreen.drawEntity(x, y, scale, (float) (x) - mouseX, (float) (y - 50) - mouseY, player);
        matrices.pop();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public static class MMOButtonInfo {

        public final Text text;
        public final ButtonWidget.PressAction onClick;
        public final int color;
        public final int hoverColor;

        public MMOButtonInfo(Text text, ButtonWidget.PressAction onClick) {
            this(text, onClick, Objects.requireNonNull(Formatting.WHITE.getColorValue()),
                    Objects.requireNonNull(Formatting.YELLOW.getColorValue()));
        }

        public MMOButtonInfo(Text text, ButtonWidget.PressAction onClick, int color, int hoverColor) {
            this.text = text;
            this.onClick = onClick;
            this.color = color;
            this.hoverColor = hoverColor;
        }
    }
}
