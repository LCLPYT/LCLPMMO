package work.lclpnet.mmo.client.gui.character;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.lclpnetwork.model.JsonSerializable;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import java.util.Objects;
import java.util.concurrent.CompletionException;

public class EditCharacterScreen extends MMOScreen {

    private ButtonWidget saveButton;
    private TextFieldWidget nameEdit;
    private final MMOCharacter character;
    private final Screen prevScreen;

    public EditCharacterScreen(MMOCharacter character, Screen prevScreen) {
        super(new TranslatableText("mmo.menu.edit_character.title"));
        this.character = character;
        this.prevScreen = prevScreen;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    protected void init() {
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(true);
        
        this.saveButton = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20,
                new TranslatableText("mmo.menu.edit_character.save"), buttonWidget -> this.saveChanges()));

        this.addButton(new ButtonWidget(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20,
                new TranslatableText("gui.cancel"), buttonWidget -> this.client.openScreen(prevScreen)));

        this.nameEdit = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 53, 200, 20,
                new TranslatableText("mmo.menu.edit_character.edit_name"));
        this.nameEdit.setText(character.getName());
        this.nameEdit.setChangedListener(s -> {
            String trimmed = s.trim();
            this.saveButton.active = !trimmed.isEmpty();
        });
        this.children.add(this.nameEdit);
        this.setFocused(this.nameEdit);
    }

    @Override
    public void resize(MinecraftClient p_resize_1_, int p_resize_2_, int p_resize_3_) {
        String s = this.nameEdit.getText();
        this.init(p_resize_1_, p_resize_2_, p_resize_3_);
        this.nameEdit.setText(s);
    }

    @Override
    public void removed() {
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(false);
    }

    private void saveChanges() {
        if (character.id == null) throw new NullPointerException("Character id is null!");

        String name = this.nameEdit.getText().trim();
        if (!character.getName().equals(name)) {
            JsonObject body = new JsonObject();
            body.addProperty("characterId", character.id);
            body.addProperty("newName", name);

            LCLPNetworkSession.getAuthorizedApi().renameCharacter(character.id, name).thenRun(() -> {
                displayToast(new TranslatableText("mmo.menu.edit_character.edit_success"));
                CharacterChooserScreen.updateContentAndShow(client, prevScreen);
            }).exceptionally(completionError -> {
                Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
                if (err == null) err = completionError;

                if (APIException.NO_CONNECTION.equals(err)) {
                    displayToast(new TranslatableText("mmo.menu.edit_character.edit_failed"),
                            new TranslatableText("mmo.no_internet"));
                } else if (err instanceof ResponseEvaluationException) {
                    APIResponse response = ((ResponseEvaluationException) err).getResponse();
                    if (response.getResponseCode() == 409) {
                        String s = response.getJsonStatusMessage();
                        if (s != null && s.equalsIgnoreCase("Name cannot be changed yet.")) {
                            String days = JsonSerializable.parse(response.getRawError(), JsonObject.class)
                                    .get("extra")
                                    .getAsString()
                                    .split(" ")[0];

                            displayToast(new TranslatableText("mmo.menu.edit_character.name_not_yet_changeable"),
                                    new TranslatableText("mmo.menu.edit_character.name_not_yet_changeable_days", days));
                        } else {
                            Text reason = new TranslatableText("error.unknown");
                            if (response.hasValidationViolations()) {
                                reason = new LiteralText(response.getValidationViolations().getFirst());
                            }

                            displayToast(new TranslatableText("mmo.menu.edit_character.edit_failed"), reason);
                        }
                    } else displayToast(new TranslatableText("mmo.menu.edit_character.edit_failed"));
                } else {
                    displayToast(new TranslatableText("mmo.menu.edit_character.edit_failed"));
                }
                return null;
            });
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("mmo.menu.edit_character.edit_name"), this.width / 2 - 100, 40, 10526880);
        this.nameEdit.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
