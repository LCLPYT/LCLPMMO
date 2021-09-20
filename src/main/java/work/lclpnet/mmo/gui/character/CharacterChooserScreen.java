package work.lclpnet.mmo.gui.character;

import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.EditableGenericSelectionScreen;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.util.QueueWorker;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class CharacterChooserScreen extends EditableGenericSelectionScreen<MMOCharacter> {

    protected final List<MMOCharacter> characters;

    protected CharacterChooserScreen(Screen prevScreen, List<MMOCharacter> characters) {
        super(new TranslationTextComponent("mmo.menu.select_character.title"), prevScreen);
        this.characters = Objects.requireNonNull(characters);
        this.background = BACKGROUND_LOCATION_ALT;

        if (LCLPNetwork.getSelectedCharacter() != null) {
            for (MMOCharacter character : characters) {
                if (character.id != null && character.id.equals(LCLPNetwork.getSelectedCharacter().id)) {
                    this.preSelected = character;
                    break;
                }
            }
        }
    }

    @Override
    public void onSelected(MMOCharacter selected) {
        if (selected.id == null) throw new IllegalStateException("Character id is null");

        JsonObject body = new JsonObject();
        body.addProperty("characterId", selected.id);

        LCLPNetwork.getAPI().setActiveCharacter(selected.id).thenRun(() -> {
            LCLPNetwork.setSelectedCharacter(selected);
            QueueWorker.enqueueOnRender(() -> Minecraft.getInstance().displayGuiScreen(new MMOMainScreen(false)));
        }).exceptionally(completionError -> {
            Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
            if (err == null) err = completionError;

            if (APIException.NO_CONNECTION.equals(err)) displayToast(new TranslationTextComponent("mmo.no_internet"));
            else if (err instanceof ResponseEvaluationException) {
                APIResponse response = ((ResponseEvaluationException) err).getResponse();
                ITextComponent reason = new TranslationTextComponent("error.unknown");
                if (response.hasValidationViolations()) reason = new StringTextComponent(response.getValidationViolations().getFirst());
                err.printStackTrace();

                displayToast(reason);
            }
            return null;
        });
    }

    @Override
    public List<MMOCharacter> getEntries() {
        return characters;
    }

    @Override
    public void addEntry() {
        assert this.minecraft != null;
        this.minecraft.displayGuiScreen(new CharacterCreatorScreen(this, characters.isEmpty()));
    }

    @Override
    public void editEntry(MMOCharacter character) {
        assert this.minecraft != null;
        this.minecraft.displayGuiScreen(new EditCharacterScreen(character, this));
    }

    @Override
    public void deleteEntry(MMOCharacter character) {
        if (character.id == null) throw new NullPointerException("Character id is null.");

        assert this.minecraft != null;

        this.minecraft.displayGuiScreen(new ConfirmScreen(yes -> {
            if (yes) deleteCharacter(character);
            else this.minecraft.displayGuiScreen(CharacterChooserScreen.this);
        }, new TranslationTextComponent("mmo.menu.select_character.confirm_delete"),
                new TranslationTextComponent("mmo.menu.select_character.confirm_delete_desc", character.getTitle())));
    }

    protected void deleteCharacter(MMOCharacter character) {
        LCLPNetwork.getAPI().deleteCharacter(character.id).handle((result, err) -> {
            if (err != null) {
                if (APIException.NO_CONNECTION.equals(err)) displayToast(new TranslationTextComponent("mmo.menu.select_character.delete_failed"),
                        new TranslationTextComponent("mmo.no_internet"));
                else if (err instanceof ResponseEvaluationException) {
                    APIResponse response = ((ResponseEvaluationException) err).getResponse();
                    if (response.getResponseCode() != 200) {
                        ITextComponent reason = new TranslationTextComponent("error.unknown");
                        System.err.println(response);
                        if (response.hasValidationViolations()) {
                            reason = new StringTextComponent(response.getValidationViolations().getFirst());
                        }

                        displayToast(new TranslationTextComponent("mmo.menu.select_character.delete_failed"), reason);
                    }
                }
            } else {
                displayToast(new TranslationTextComponent("mmo.menu.select_character.delete_success"),
                        null);
                if (LCLPNetwork.getSelectedCharacter() != null && LCLPNetwork.getSelectedCharacter().id != null && character.id.equals(LCLPNetwork.getSelectedCharacter().id))
                    LCLPNetwork.setSelectedCharacter(null);
            }
            CharacterChooserScreen.updateContentAndShow(minecraft, prevScreen);
            return result;
        });
    }

    @Override
    public void copyEntry(MMOCharacter character) {

    }

    public static void updateContentAndShow(final Minecraft mc, Screen prevScreen) {
        updateContentAndShow(mc, prevScreen, false);
    }

    public static void updateContentAndShow(final Minecraft mc, Screen prevScreen, boolean updateActiveCharacter) {
        final Consumer<List<MMOCharacter>> update = characters ->
                QueueWorker.enqueueOnRender(() -> mc.displayGuiScreen(new CharacterChooserScreen(prevScreen, characters)));

        LCLPNetwork.getAPI().getCharacters().thenAccept(characters -> {
            if (LCLPNetwork.getSelectedCharacter() == null || updateActiveCharacter) LCLPNetwork.loadActiveCharacter()
                    .thenRun(() -> update.accept(characters))
                    .exceptionally(err -> {
                        err.printStackTrace();
                        return null;
                    });
            else update.accept(characters);
        }).exceptionally(err -> {
            update.accept(new ArrayList<>());
            return null;
        });
    }
}
