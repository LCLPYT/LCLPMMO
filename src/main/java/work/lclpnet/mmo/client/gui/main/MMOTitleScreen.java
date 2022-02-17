package work.lclpnet.mmo.client.gui.main;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
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

@Environment(EnvType.CLIENT)
public class MMOTitleScreen extends MMOScreen {

    public static final CubeMapRenderer PANORAMA_RESOURCES = new CubeMapRenderer(LCLPMMO.identifier("textures/gui/main/panorama"));
    private static final Identifier PANORAMA_OVERLAY_TEXTURES = LCLPMMO.identifier("textures/gui/main/panorama_overlay.png"),
            MINECRAFT_TITLE_TEXTURES = LCLPMMO.identifier("textures/gui/main/title.png"),
            ACCESSIBILITY_TEXTURES = new Identifier("textures/gui/accessibility.png");

    private final RotatingCubeMapRenderer panorama = new RotatingCubeMapRenderer(PANORAMA_RESOURCES);
    private final boolean showFadeInAnimation;
    private long firstRenderTime = 0L;
    private final List<MMOButtonInfo> menuButtons = new ArrayList<>();
    private ClientPlayerEntity player;

    public MMOTitleScreen(boolean fade) {
        super(new LiteralText("Main menu"));
        this.showFadeInAnimation = fade;

        setupButtons();
    }

    private void setupEntity() {
        if (client == null || (player != null && client.getNetworkHandler() != null && !hasGameProfileChanged()))
            return;

        ClientPlayNetworkHandler netHandler = new FakeClientPlayNetworkHandler(client);
        ClientWorld world = new FakeClientWorld(netHandler, new ClientWorld.Properties(Difficulty.NORMAL, false, false));
        player = new ClientPlayerEntity(client, world, netHandler, null, null, false, false);
        MMOClient.initializeMyPlayer(player);

        TrackedData<Byte> PLAYER_MODEL_FLAG = PlayerEntityAccessor.getPlayerModelFlag();
        int modelParts = 0;
        for (PlayerModelPart part : client.options.getEnabledPlayerModelParts())
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
            this.addButton(btn);
            currentBtnY += btnHeight + btnVSpacing;
        }

        final int quitY = (int) (this.height * 0.9);

        final int imgBtnDim = 20;
        final int imgBtnY = quitY - btnVSpacing - imgBtnDim;

        this.addButton(new TexturedButtonWidget(currentBtnX,
                imgBtnY,
                imgBtnDim,
                imgBtnDim,
                0,
                106,
                20,
                ButtonWidget.WIDGETS_LOCATION,
                256,
                256,
                b -> this.client.openScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager())),
                new TranslatableText("narrator.button.language")));

        this.addButton(new TexturedButtonWidget(currentBtnX + 25,
                imgBtnY,
                imgBtnDim,
                imgBtnDim,
                0,
                0,
                20,
                ACCESSIBILITY_TEXTURES,
                32,
                64,
                b -> this.client.openScreen(new AccessibilityOptionsScreen(this, this.client.options)),
                new TranslatableText("narrator.button.accessibility")));

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

        this.addButton(quitButton);

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
        this.addButton(logoutBtn);
    }

    private void setupButtons() {
        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.singleplayer"), b -> {
            if (this.client != null)
                this.client.openScreen(new SelectWorldScreen(this));
        }));
        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.multiplayer"), b -> {
            if (this.client == null) return;

            if (this.client.options.skipMultiplayerWarning) {
                this.client.openScreen(new MultiplayerScreen(this));
            } else {
                this.client.openScreen(new MultiplayerWarningScreen(this));
            }
        }));
        this.menuButtons.add(new MMOButtonInfo(new TranslatableText("menu.options"), b -> {
            if (this.client != null)
                this.client.openScreen(new OptionsScreen(this, this.client.options));
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
                this.client.openScreen(new MMOTitleScreen(false));
            } else {
                this.client.openScreen(new LoginScreen());
            }
        }).exceptionally(err -> {
            this.client.openScreen(new LoginScreen());
            err.printStackTrace();
            return null;
        });
    }

    protected void doLogout() {
        if (this.client == null) throw new AssertionError();

        this.client.openScreen(new ConfirmScreen(accepted -> {
            if (accepted) {
                MMOClient.logout().exceptionally(err -> {
                    err.printStackTrace();
                    return null;
                });
                this.client.openScreen(new LoginScreen());
            } else {
                this.client.openScreen(MMOTitleScreen.this);
            }
        }, new TranslatableText("mmo.menu.confirm_logout"), new TranslatableText("mmo.menu.confirm_logout_desc")));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(@Nonnull MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        if (this.client == null) throw new AssertionError();

        if (this.firstRenderTime == 0L && this.showFadeInAnimation)
            this.firstRenderTime = Util.getMeasuringTimeMs();

        // Overlay
        float alphaRaw = this.showFadeInAnimation ? (float) (Util.getMeasuringTimeMs() - this.firstRenderTime) / 1000.0F : 1.0F;
        this.panorama.render(partialTicks, MathHelper.clamp(alphaRaw, 0.0F, 1.0F));

        // Panorama Overlay
        this.client.getTextureManager().bindTexture(PANORAMA_OVERLAY_TEXTURES);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.showFadeInAnimation ? (float) MathHelper.ceil(MathHelper.clamp(alphaRaw, 0.0F, 1.0F)) : 1.0F);
        drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

        float alpha = this.showFadeInAnimation ? MathHelper.clamp(alphaRaw - 1.0F, 0.0F, 1.0F) : 1.0F;
        int l = MathHelper.ceil(alpha * 255.0F) << 24;
        if ((l & -67108864) != 0) {  // Prevent "flicker" when fading
            final float scale = 1F / (360F / this.height);

            // Logo
            this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
            final int logoDim = (int) (this.width * 0.05);
            matrices.push();
            matrices.scale(scale, scale, scale);
            drawTexture(matrices, (int) (logoDim / scale), (int) (logoDim / scale), 0, 0, 255, 84);
            matrices.pop();

            // Player Model
            this.drawPlayerModel(mouseX, mouseY, alpha);

            // Button Opacity
            for (AbstractButtonWidget widget : this.buttons)
                widget.setAlpha(alpha);

            // super
            super.render(matrices, mouseX, mouseY, partialTicks);
        }
    }

    @SuppressWarnings("deprecation")
    protected void drawPlayerModel(int mouseX, int mouseY, float alpha) {
        if (client == null) throw new AssertionError();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
        int x = (int) (this.width * 0.8), y = (int) (this.height * 0.8);
        int scale = (int) (100F * (this.height / 360F));

        mouseY = MathHelper.clamp(mouseY, (int) (this.height * 0.6F), (int) (this.height * 0.75F));
//        mouseY += playerMouseCorrection;

        final float zOffset = 60F;

        RenderSystem.translatef(0F, 0F, zOffset);
        InventoryScreen.drawEntity(x, y, scale, (float) (x) - mouseX, (float) (y - 50) - mouseY, player);
        RenderSystem.translatef(0F, 0F, -zOffset);
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
