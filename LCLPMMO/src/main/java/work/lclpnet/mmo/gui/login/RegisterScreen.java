package work.lclpnet.mmo.gui.login;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.util.AuthManager;
import work.lclpnet.mmo.util.Color;

import java.net.URI;
import java.net.URISyntaxException;

public class RegisterScreen extends MMOScreen {

    private TextFieldWidget textFieldEmail;
    private TextFieldWidget textFieldPassword;
    private TextFieldWidget textFieldConfirmPassword;
    private Button buttonRegister;
    private final AuthManager authManager = new AuthManager();
    private boolean registerFailed = false, passwordError = false, mailError = false;
    private ResponsiveCheckboxButton checkbox;
    private final String privPolText;
    private final int privPolX = 10, privPolY = 10;
    private final ITextComponent privPol;

    public RegisterScreen() {
        super(new TranslationTextComponent("mmo.menu.register.title"));

        privPolText = I18n.format("mmo.menu.register.privacy_policy");
        privPol = new StringTextComponent(privPolText);
        privPol.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://lclpnet.work/datenschutz"));
        privPol.getStyle().setUnderlined(true);
        privPol.getStyle().setColor(TextFormatting.BLUE);
    }

    public void tick() {
        this.textFieldEmail.tick();
        this.textFieldPassword.tick();
        this.textFieldConfirmPassword.tick();
    }

    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.textFieldEmail = new TextFieldWidget(this.font, this.width / 2 - 100, 56, 200, 20, I18n.format("mmo.menu.register.email"));
        this.textFieldEmail.setFocused2(true);
        this.textFieldEmail.setText("");
        this.textFieldEmail.setResponder(this::changed);
        this.children.add(this.textFieldEmail);

        this.textFieldPassword = new PasswordTextField(this.font, this.width / 2 - 100, 96, 200, 20, I18n.format("mmo.menu.register.password"));
        this.textFieldPassword.setText("");
        this.textFieldPassword.setResponder(this::changed);
        this.children.add(this.textFieldPassword);

        this.textFieldConfirmPassword = new PasswordTextField(this.font, this.width / 2 - 100, 136, 200, 20, I18n.format("mmo.menu.register.password_confirm"));
        this.textFieldConfirmPassword.setText("");
        this.textFieldConfirmPassword.setResponder(this::changed);
        this.children.add(this.textFieldConfirmPassword);

        this.checkbox = new ResponsiveCheckboxButton(this.width / 2 - 100, 160, 150, 20, I18n.format("mmo.menu.register.accept"), false);
        this.checkbox.setResponder(this::changed);
        this.addButton(checkbox);

        this.buttonRegister = this.addButton(new Button(this.width / 2 - 100, 184, 200, 20, I18n.format("mmo.menu.register.register"), (p_213030_1_) -> {
            if(!textFieldPassword.getText().equals(textFieldConfirmPassword.getText())) {
                SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
                        new TranslationTextComponent("mmo.menu.register.password_mismatch"),
                        null);
                passwordError = true;
                return;
            }
            passwordError = false;

            this.buttonRegister.active = false;
            authManager.register(textFieldEmail.getText(), textFieldPassword.getText(), textFieldConfirmPassword.getText(), error -> {
                this.buttonRegister.active = true;

                if(error == null) {
                    SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
                            new TranslationTextComponent("mmo.menu.register.success"), null);
                    LoginScreen.resolve(this.minecraft);
                }
                else {
                    registerFailed = false;
                    mailError = false;
                    passwordError = false;

                    ITextComponent err = null;
                    if(error.equals("The email has already been taken.")) {
                        err = new TranslationTextComponent("mmo.menu.register.mail_taken");
                        mailError = true;
                    }
                    else if(error.equals("The password must be at least 8 characters.")) {
                        err = new TranslationTextComponent("mmo.menu.register.pw_too_short");
                        passwordError = true;
                    }
                    else {
                        err = new StringTextComponent(error);
                        registerFailed = true;
                    }

                    SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
                            new TranslationTextComponent("mmo.menu.login.login_failed"),
                            err);
                }
            });
        }));

        this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 18 + 10, 200, 20, I18n.format("mmo.menu.register.login"), (p_213029_1_) -> {
            this.minecraft.displayGuiScreen(new LoginScreen());
        }));

        this.validate();
    }

    private void changed(Object o) {
        this.validate();
    }

    private void validate() {
        String email = this.textFieldEmail.getText();
        this.buttonRegister.active =
                !email.isEmpty() && email.split("@").length == 2
                && !this.textFieldPassword.getText().isEmpty()
                && !this.textFieldConfirmPassword.getText().isEmpty()
                && this.checkbox.isChecked();
    }

    @Override
    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.textFieldEmail.getText();
        String s1 = this.textFieldPassword.getText();
        String s2 = this.textFieldConfirmPassword.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.textFieldEmail.setText(s);
        this.textFieldPassword.setText(s1);
        this.textFieldConfirmPassword.setText(s2);
    }

    @Override
    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    @Override
    public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 17, 16777215);
        this.drawString(this.font, I18n.format("mmo.menu.login.email"), this.width / 2 - 100, 44, registerFailed | mailError ? Color.RED : 10526880);
        this.drawString(this.font, I18n.format("mmo.menu.login.password"), this.width / 2 - 100, 84, registerFailed || passwordError ? Color.RED : 10526880);
        this.drawString(this.font, I18n.format("mmo.menu.register.password_confirm"), this.width / 2 - 100, 124, registerFailed || passwordError ? Color.RED : 10526880);

        this.font.drawStringWithShadow(privPol.getFormattedText(), privPolX, privPolY, 0xFFFFFF);

        this.textFieldEmail.render(p_render_1_, p_render_2_, p_render_3_);
        this.textFieldPassword.render(p_render_1_, p_render_2_, p_render_3_);
        this.textFieldConfirmPassword.render(p_render_1_, p_render_2_, p_render_3_);
        super.render(p_render_1_, p_render_2_, p_render_3_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        if (super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_)) {
            return true;
        } else if (mouseX > (double) privPolX
                && mouseX < (double) (privPolX + this.font.getStringWidth(privPolText))
                && mouseY > (double) privPolY
                && mouseY < (double) (privPolY + this.font.FONT_HEIGHT)) {
            try {
                URI uri = new URI("https://lclpnet.work/datenschutz");
                Util.getOSType().openURI(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
