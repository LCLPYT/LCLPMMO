package work.lclpnet.mmo.gui.login;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.event.EventListener;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.util.Color;
import work.lclpnet.mmo.util.network.AuthManager;
import work.lclpnet.mmo.util.network.LCLPNetwork;

public class LoginScreen extends MMOScreen {

    private TextFieldWidget textFieldEmail;
    private TextFieldWidget textFieldPassword;
    private Button buttonLogin;
    private final AuthManager authManager = new AuthManager();
    private boolean loginFailed = false;

    public LoginScreen() {
        super(new TranslationTextComponent("mmo.menu.login.title"));
    }

    public void tick() {
        this.textFieldEmail.tick();
        this.textFieldPassword.tick();
    }

    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.textFieldEmail = new TextFieldWidget(this.font, this.width / 2 - 100, 76, 200, 20, new TranslationTextComponent("mmo.menu.login.email"));
        this.textFieldEmail.setFocused2(true);
        this.textFieldEmail.setText("");
        this.textFieldEmail.setResponder(this::changed);
        this.children.add(this.textFieldEmail);

        this.textFieldPassword = new PasswordTextField(this.font, this.width / 2 - 100, 116, 200, 20, new TranslationTextComponent("mmo.menu.login.password"));
        this.textFieldPassword.setText("");
        this.textFieldPassword.setResponder(this::changed);
        this.children.add(this.textFieldPassword);

        this.buttonLogin = this.addButton(new Button(this.width / 2 - 100, 146, 200, 20, new TranslationTextComponent("mmo.menu.login.login"), (p_213030_1_) -> {
            this.buttonLogin.active = false;
            authManager.login(textFieldEmail.getText(), textFieldPassword.getText(), success -> {
                this.buttonLogin.active = true;
                if (success == null) {
                    displayToast(new TranslationTextComponent("mmo.menu.login.login_failed"),
                            new TranslationTextComponent("mmo.no_internet"));
                } else if (success) {
                    displayToast(new TranslationTextComponent("mmo.menu.login.login_successful"));
                    loadUserAndResolve(this.minecraft);
                } else {
                    loginFailed = true;
                    displayToast(new TranslationTextComponent("mmo.menu.login.login_failed"),
                            new TranslationTextComponent("mmo.menu.login.check_credentials"));
                }
            });
        }));

        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 18 + 10, 200, 20, new TranslationTextComponent("mmo.menu.login.register"), (p_213031_1_) -> {
            this.minecraft.displayGuiScreen(new RegisterScreen());
        }));

        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18 + 10, 200, 20, new TranslationTextComponent("mmo.menu.login.play_offline"), (p_213029_1_) -> {
            resolve(this.minecraft);
        }));

        this.validate();
    }

    public static void resolve(Minecraft mc) {
        Screen startingScreen = EventListener.getStartingScreen();
        if (startingScreen instanceof PreIntroScreen) ((PreIntroScreen) startingScreen).renderBG = true;
        mc.displayGuiScreen(startingScreen);
    }

    public static void loadUserAndResolve(Minecraft mc) {
        LCLPNetwork.checkAccessToken(user -> User.reloadUser(user, () -> resolve(mc)));
    }

    private void changed(String s) {
        this.validate();
    }

    private void validate() {
        String email = this.textFieldEmail.getText();
        boolean flag = !email.isEmpty() && email.split("@").length == 2;
        this.buttonLogin.active = flag && !this.textFieldPassword.getText().isEmpty();
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.textFieldEmail.getText();
        String s1 = this.textFieldPassword.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.textFieldEmail.setText(s);
        this.textFieldPassword.setText(s1);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        drawCenteredString(mStack, this.font, this.title, this.width / 2, 17, 16777215);
        drawCenteredString(mStack, this.font, I18n.format("mmo.menu.login.desc"), this.width / 2, 34, 10526880);
        drawString(mStack, this.font, I18n.format("mmo.menu.login.email"), this.width / 2 - 100, 63, loginFailed ? Color.RED : 10526880);
        drawString(mStack, this.font, I18n.format("mmo.menu.login.password"), this.width / 2 - 100, 104, loginFailed ? Color.RED : 10526880);
        drawString(mStack, this.font, I18n.format("mmo.menu.login.no_acc"), this.width / 2 - 100, this.height / 4 + 96 + 18 - 2, 10526880);
        this.textFieldEmail.render(mStack, mouseX, mouseY, partialTicks);
        this.textFieldPassword.render(mStack, mouseX, mouseY, partialTicks);
        super.render(mStack, mouseX, mouseY, partialTicks);
    }
}
