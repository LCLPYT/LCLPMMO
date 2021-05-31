package work.lclpnet.mmo.gui.character;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.MMOScreen;
import work.lclpnet.mmo.util.network.LCLPNetwork;

public class EditCharacterScreen extends MMOScreen {

    private Button saveButton;
    private TextFieldWidget nameEdit;
    private final MMOCharacter character;
    private final Screen prevScreen;

    public EditCharacterScreen(MMOCharacter character, Screen prevScreen) {
        super(new TranslationTextComponent("mmo.menu.edit_character.title"));
        this.character = character;
        this.prevScreen = prevScreen;
    }

    public void tick() {
        this.nameEdit.tick();
    }

    protected void init() {
        this.minecraft.keyboardListener.enableRepeatEvents(true);
        this.saveButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, new TranslationTextComponent("mmo.menu.edit_character.save"), (p_214308_1_) -> {
            this.saveChanges();
        }));
        this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, new TranslationTextComponent("gui.cancel"), (p_214306_1_) -> {
            this.minecraft.displayGuiScreen(prevScreen);
        }));
        this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 53, 200, 20, new TranslationTextComponent("mmo.menu.edit_character.edit_name"));
        this.nameEdit.setText(character.getName());
        this.nameEdit.setResponder((p_214301_1_) -> {
            String trimmed = p_214301_1_.trim();
            this.saveButton.active = !trimmed.isEmpty();
        });
        this.children.add(this.nameEdit);
        this.setFocusedDefault(this.nameEdit);
    }

    public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.nameEdit.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.nameEdit.setText(s);
    }

    public void removed() {
        this.minecraft.keyboardListener.enableRepeatEvents(false);
    }

    private void saveChanges() {
        if (character.id == null) throw new NullPointerException("Character id is null!");

        String name = this.nameEdit.getText().trim();
        if (!character.getName().equals(name)) {
            JsonObject body = new JsonObject();
            body.addProperty("characterId", character.id);
            body.addProperty("newName", name);

            LCLPNetwork.post("api/ls5/rename-character", body, response -> {
                if (response.isNoConnection()) {
                    displayToast(new TranslationTextComponent("mmo.menu.edit_character.edit_failed"),
                            new TranslationTextComponent("mmo.no_internet"));
                } else if (response.getResponseCode() == 200) {
                    displayToast(new TranslationTextComponent("mmo.menu.edit_character.edit_success"));
                    CharacterChooserScreen.updateContentAndShow(minecraft, prevScreen);
                } else {
                    String s = response.getJsonStatusMessage();
                    if (s != null && s.equalsIgnoreCase("Name cannot be changed yet.")) {
                        String days = JsonSerializable.parse(response.getRawError(), JsonObject.class)
                                .get("extra")
                                .getAsString()
                                .split(" ")[0];

                        displayToast(new TranslationTextComponent("mmo.menu.edit_character.name_not_yet_changeable"),
                                new TranslationTextComponent("mmo.menu.edit_character.name_not_yet_changeable_days", days));
                    } else {
                        System.err.println(response);

                        ITextComponent reason = new TranslationTextComponent("error.unknown");
                        if (response.hasValidationViolations()) {
                            reason = new StringTextComponent(response.getValidationViolations().getFirst());
                        }

                        displayToast(new TranslationTextComponent("mmo.menu.edit_character.edit_failed"), reason);
                    }
                }
            });
        }
    }

    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        drawCenteredString(mStack, this.font, this.title, this.width / 2, 20, 16777215);
        drawString(mStack, this.font, I18n.format("mmo.menu.edit_character.edit_name"), this.width / 2 - 100, 40, 10526880);
        this.nameEdit.render(mStack, mouseX, mouseY, partialTicks);
        super.render(mStack, mouseX, mouseY, partialTicks);
    }
}
