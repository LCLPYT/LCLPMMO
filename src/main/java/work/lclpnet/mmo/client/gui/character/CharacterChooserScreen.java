package work.lclpnet.mmo.client.gui.character;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.mmo.client.MMOClient;
import work.lclpnet.mmo.client.gui.main.MMOTitleScreen;
import work.lclpnet.mmo.client.gui.select.EditableGenericSelectionScreen;
import work.lclpnet.mmo.client.util.RenderWorker;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CharacterChooserScreen extends EditableGenericSelectionScreen<CharacterSelectionItem> {

    protected final List<CharacterSelectionItem> characters;

    protected CharacterChooserScreen(Screen previousScreen, List<MMOCharacter> characters) {
        super(new TranslatableText("mmo.menu.select_character.title"), previousScreen);

        this.characters = Objects.requireNonNull(characters).stream()
                .map(CharacterSelectionItem::new)
                .collect(Collectors.toList());

        this.background = BACKGROUND_LOCATION_ALT;

        final MMOCharacter active = MMOClient.getActiveCharacter();
        if (active != null) {
            for (CharacterSelectionItem character : this.characters) {
                if (character.getCharacter().id != null && character.getCharacter().id.equals(active.id)) {
                    this.preSelected = character;
                    break;
                }
            }
        }
    }

    @Override
    public void onSelected(CharacterSelectionItem selected) {
        final MMOCharacter character = selected.getCharacter();
        if (character.id == null) throw new IllegalStateException("Character id is null");

        JsonObject body = new JsonObject();
        body.addProperty("characterId", character.id);

        LCLPNetworkSession.getAuthorizedApi().setActiveCharacter(character.id).thenRun(() -> {
            MMOClient.setActiveCharacter(character);
            MMOClient.logActiveCharacterLoaded(character);
            RenderWorker.push(() -> MinecraftClient.getInstance().openScreen(new MMOTitleScreen(false)));
        }).exceptionally(completionError -> {
            Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
            if (err == null) err = completionError;

            if (APIException.NO_CONNECTION.equals(err)) {
                displayToast(new TranslatableText("mmo.no_internet"));
            }
            else if (err instanceof ResponseEvaluationException) {
                APIResponse response = ((ResponseEvaluationException) err).getResponse();
                Text reason = new TranslatableText("error.unknown");
                if (response.hasValidationViolations())
                    reason = new LiteralText(response.getValidationViolations().getFirst());

                err.printStackTrace();

                displayToast(reason);
            }
            return null;
        });
    }

    @Override
    public List<CharacterSelectionItem> getEntries() {
        return characters;
    }

    @Override
    public void addEntry() {
        Objects.requireNonNull(this.client).openScreen(new CharacterCreatorScreen(success -> {
            if (success) CharacterChooserScreen.updateContentAndShow(this.client, this.getPreviousScreen(), characters.isEmpty());
            else this.client.openScreen(this);
        }));
    }

    @Override
    public void editEntry(CharacterSelectionItem item) {
        Objects.requireNonNull(this.client).openScreen(new EditCharacterScreen(item.getCharacter(), this));
    }

    @Override
    public void deleteEntry(CharacterSelectionItem item) {
        if (item.getCharacter().id == null) throw new NullPointerException("Character id is null.");

        Objects.requireNonNull(this.client).openScreen(new ConfirmScreen(yes -> {
            if (yes) deleteCharacter(item.getCharacter());
            else this.client.openScreen(CharacterChooserScreen.this);
        }, new TranslatableText("mmo.menu.select_character.confirm_delete"),
                new TranslatableText("mmo.menu.select_character.confirm_delete_desc", item.getTitle())));
    }

    protected void deleteCharacter(MMOCharacter character) {
        LCLPNetworkSession.getAuthorizedApi().deleteCharacter(character.id).handle((result, err) -> {
            if (err != null) {
                if (APIException.NO_CONNECTION.equals(err)) displayToast(new TranslatableText("mmo.menu.select_character.delete_failed"),
                        new TranslatableText("mmo.no_internet"));
                else if (err instanceof ResponseEvaluationException) {
                    APIResponse response = ((ResponseEvaluationException) err).getResponse();
                    if (response.getResponseCode() != 200) {
                        Text reason = new TranslatableText("error.unknown");
                        if (response.hasValidationViolations()) {
                            reason = new LiteralText(response.getValidationViolations().getFirst());
                        }

                        displayToast(new TranslatableText("mmo.menu.select_character.delete_failed"), reason);
                    }
                }
            } else {
                displayToast(new TranslatableText("mmo.menu.select_character.delete_success"));
                MMOCharacter activeCharacter = MMOClient.getActiveCharacter();
                if (activeCharacter != null && activeCharacter.id != null && character.id.equals(activeCharacter.id))
                    MMOClient.setActiveCharacter(null);
            }
            CharacterChooserScreen.updateContentAndShow(client, previousScreen);
            return result;
        });
    }

    @Override
    public void copyEntry(CharacterSelectionItem character) {
        // NO-OP
    }

    public static void updateContentAndShow(final MinecraftClient mc, Screen prevScreen) {
        updateContentAndShow(mc, prevScreen, false);
    }

    public static void updateContentAndShow(final MinecraftClient mc, Screen prevScreen, boolean updateActiveCharacter) {
        MMOClient.fetchAndCacheCharacters(updateActiveCharacter)
                .exceptionally(err -> new ArrayList<>())
                .thenAccept(characters -> {
                    RenderWorker.push(() -> mc.openScreen(new CharacterChooserScreen(prevScreen, characters)));
                });
    }
}
