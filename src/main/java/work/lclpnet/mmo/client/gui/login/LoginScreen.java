package work.lclpnet.mmo.client.gui.login;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.client.gui.main.PreIntroScreen;
import work.lclpnet.mmo.client.module.TitleScreenClientModule;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.client.util.SingleRequestManager;

import java.util.Objects;
import java.util.concurrent.CompletionException;

import static net.minecraft.client.resource.language.I18n.translate;

public class LoginScreen extends MMOScreen {

    private TextFieldWidget textFieldEmail;
    private TextFieldWidget textFieldPassword;
    private ButtonWidget buttonLogin;
    private final SingleRequestManager authManager = new SingleRequestManager();
    private boolean loginFailed = false;

    public LoginScreen() {
        super(new TranslatableText("mmo.menu.login.title"));
    }

    @Override
    public void tick() {
        this.textFieldEmail.tick();
        this.textFieldPassword.tick();
    }

    @Override
    protected void init() {
        Objects.requireNonNull(client);

        this.client.keyboard.setRepeatEvents(true);
        this.textFieldEmail = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 76, 200, 20, new TranslatableText("mmo.menu.login.email"));
        this.textFieldEmail.setTextFieldFocused(true);
        this.textFieldEmail.setText("");
        this.textFieldEmail.setChangedListener(this::changed);
        this.addDrawableChild(this.textFieldEmail);

        this.textFieldPassword = new PasswordTextField(this.textRenderer, this.width / 2 - 100, 116, 200, 20, new TranslatableText("mmo.menu.login.password"));
        this.textFieldPassword.setText("");
        this.textFieldPassword.setChangedListener(this::changed);
        this.addDrawableChild(this.textFieldPassword);

        this.buttonLogin = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, 146, 200, 20, new TranslatableText("mmo.menu.login.login"), this::login));

        ButtonWidget registerButton = new ButtonWidget(this.width / 2 - 100, this.height / 4 + 96 + 18 + 10, 200, 20,
                new TranslatableText("mmo.menu.login.register"), button -> {
        },
                (button, matrices, mouseX, mouseY) -> LoginScreen.this.renderTooltip(matrices, new TranslatableText("unavailable.temporary"), mouseX, mouseY));
        registerButton.active = false; // disabled until REST API provides a new register mechanism
        this.addDrawableChild(registerButton);

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 18 + 10, 200, 20,
                new TranslatableText("mmo.menu.login.play_offline"), buttonWidget -> finishLogin(this.client)));

        this.validate();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String email = this.textFieldEmail.getText(),
                password = this.textFieldPassword.getText();

        super.resize(client, width, height);

        this.textFieldEmail.setText(email);
        this.textFieldPassword.setText(password);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (client != null) this.client.keyboard.setRepeatEvents(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 17, 16777215);
        drawCenteredText(matrices, this.textRenderer, translate("mmo.menu.login.desc"), this.width / 2, 34, 10526880);
        drawStringWithShadow(matrices, this.textRenderer, translate("mmo.menu.login.email"), this.width / 2 - 100, 63, loginFailed ? Color.RED : 10526880);
        drawStringWithShadow(matrices, this.textRenderer, translate("mmo.menu.login.password"), this.width / 2 - 100, 104, loginFailed ? Color.RED : 10526880);
        drawStringWithShadow(matrices, this.textRenderer, translate("mmo.menu.login.no_acc"), this.width / 2 - 100, this.height / 4 + 96 + 18 - 2, 10526880);
        this.textFieldEmail.render(matrices, mouseX, mouseY, delta);
        this.textFieldPassword.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void login(ButtonWidget buttonWidget) {
        this.buttonLogin.active = false;

        // TODO make stateful and add loading indicator

        authManager.login(textFieldEmail.getText(), textFieldPassword.getText()).thenAccept(success -> {
            if (success == null) return; // already in progress

            this.buttonLogin.active = true;

            if (success) {
                displayToast(new TranslatableText("mmo.menu.login.login_successful"));
                finishLogin(this.client);
            } else {
                loginFailed = true;
                displayToast(new TranslatableText("mmo.menu.login.login_failed"), new TranslatableText("mmo.menu.login.check_credentials"));
            }
        }).exceptionally(completionError -> {
            Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
            if (err == null) err = completionError;

            loginFailed = true;
            if (APIException.NO_CONNECTION.equals(err)) {
                displayToast(new TranslatableText("mmo.menu.login.login_failed"), new TranslatableText("mmo.no_internet"));
            } else {
                err.printStackTrace();
                displayToast(new TranslatableText("mmo.menu.login.login_failed"));
            }
            return null;
        });
    }

    protected void changed(String s) {
        this.validate();
    }

    private void validate() {
        String email = this.textFieldEmail.getText();
        boolean emailValid = !email.isEmpty() && email.split("@").length == 2;
        this.buttonLogin.active = emailValid && !this.textFieldPassword.getText().isEmpty();
    }

    public static void finishLogin(MinecraftClient mc) {
        Screen startingScreen = TitleScreenClientModule.getStartScreen();
        if (startingScreen instanceof PreIntroScreen) ((PreIntroScreen) startingScreen).renderBG = true;
        mc.setScreen(startingScreen);
    }
}
