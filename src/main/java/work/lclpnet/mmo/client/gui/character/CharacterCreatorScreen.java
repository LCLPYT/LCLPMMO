package work.lclpnet.mmo.client.gui.character;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import work.lclpnet.lclpnetwork.api.APIError;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.mmo.client.gui.MMOScreen;
import work.lclpnet.mmo.client.util.Color;
import work.lclpnet.mmo.data.race.MMORace;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import java.util.Objects;
import java.util.concurrent.CompletionException;

import static net.minecraft.client.resource.language.I18n.translate;

public class CharacterCreatorScreen extends MMOScreen {

    protected Text error = null;
    protected ButtonWidget btnRaceSel;
    protected ButtonWidget btnCreateCharacter;
    protected TextFieldWidget characterNameField;
    protected String characterName = "";
    protected final CharacterChooserScreen prevScreen;
    protected MMORace selectedRace = null;
    protected boolean createFirst;

    public CharacterCreatorScreen(CharacterChooserScreen prevScreen, boolean createFirst) {
        super(new TranslatableText("mmo.menu.create_character.title"));
        this.prevScreen = prevScreen;
        this.createFirst = createFirst;
    }

    @Override
    protected void init() {
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(true);

        this.characterNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, 
                new TranslatableText("mmo.menu.create_character.character_name"));
        this.characterNameField.setText(this.characterName);
        this.characterNameField.setChangedListener(s -> {
            this.characterName = s.trim();
            this.validate();
        });
        this.children.add(this.characterNameField);

        this.btnRaceSel = this.addButton(new ButtonWidget(this.width / 2 - 75, 110, 150, 20,
                new TranslatableText("mmo.menu.create_character.choose_race"),
                buttonWidget -> this.client.openScreen(new RaceSelectionScreen(this))));
        this.btnCreateCharacter = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("mmo.menu.create_character.create"), buttonWidget -> this.createCharacter()));
        this.btnCreateCharacter.active = !this.characterName.isEmpty();
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, new TranslatableText("gui.cancel"), (p_214317_1_) -> this.client.openScreen(this.prevScreen)));

        this.setFocused(this.characterNameField);
    }

    @Override
    public void tick() {
        this.characterNameField.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(BACKGROUND_LOCATION_ALT);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, -1);

        drawStringWithShadow(matrices, this.textRenderer, translate("mmo.menu.create_character.character_name"), this.width / 2 - 100, 47, -6250336);
        this.characterNameField.render(matrices, mouseX, mouseY, delta);
        if (error != null)
            drawTextWithShadow(matrices, this.textRenderer, error, this.width / 2 - 100, 85, Color.fromARGBInt(Color.RED).toARGBInt());

        drawStringWithShadow(matrices, this.textRenderer, translate("mmo.menu.create_character.choose_race_label"), this.width / 2 - 75, 98, -6250336);
        if (this.selectedRace != null)
            this.btnRaceSel.setMessage(new LiteralText(String.format("\u270E %s", this.selectedRace.getTitle().getString())));

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        super.onClose();
        Objects.requireNonNull(this.client).keyboard.setRepeatEvents(false);
    }

    public void setError(Text error) {
        this.error = error;
    }

    private boolean validate() {
        boolean valid = !this.characterName.isEmpty() && this.selectedRace != null;

        this.btnCreateCharacter.active = valid;
        return valid;
    }

    public void createCharacter() {
        if (!validate()) return;

        // TODO make stateful, add loading indicator

        LCLPNetworkSession.getAuthorizedApi().addCharacter(characterName, this.selectedRace.getUnlocalizedName()).thenRun(() -> {
            displayToast(new TranslatableText("mmo.menu.create_character.created"));
            CharacterChooserScreen.updateContentAndShow(this.client, prevScreen.getPreviousScreen(), createFirst);
        }).exceptionally(completionError -> {
            Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
            if (err == null) err = completionError;

            if (APIException.NO_CONNECTION.equals(err)) {
                displayToast(new TranslatableText("mmo.menu.create_character.error_creation_failed"),
                        new TranslatableText("mmo.no_internet"));
            } else if (err instanceof ResponseEvaluationException) {
                APIResponse response = ((ResponseEvaluationException) err).getResponse();
                if (response.getResponseCode() == 201) return null;

                Text reason;
                if (response.hasValidationViolations()) {
                    APIError violations = response.getValidationViolations();
                    if (violations.has("name", "The name has already been taken.")) {
                        reason = new TranslatableText("mmo.menu.create_character.error_name_taken");
                    } else {
                        reason = new LiteralText(violations.getFirst());
                    }
                } else if (response.hasJsonStatusMessage() && "Too many characters.".equals(response.getJsonStatusMessage())) {
                    reason = new TranslatableText("mmo.menu.create_character.error_too_many");
                } else {
                    System.err.println(response);
                    reason = new TranslatableText("error.unknown");
                }

                displayToast(new TranslatableText("mmo.menu.create_character.error_creation_failed"), reason);

                this.setError(reason);
            }
            return null;
        });
    }

    public void setSelectedRace(MMORace selected) {
        this.selectedRace = selected;
        this.validate();
    }
}
