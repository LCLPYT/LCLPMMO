package work.lclpnet.mmo.gui.character;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.EditableGenericSelectionScreen;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.util.Enqueuer;
import work.lclpnet.mmo.util.LCLPNetwork;
import work.lclpnet.mmo.util.User;

public class CharacterChooserScreen extends EditableGenericSelectionScreen<MMOCharacter> {

	protected final List<MMOCharacter> characters;

	protected CharacterChooserScreen(Screen prevScreen, List<MMOCharacter> characters) {
		super(new TranslationTextComponent("mmo.menu.select_character.title"), prevScreen);
		this.characters = Objects.requireNonNull(characters);
		this.background = BACKGROUND_LOCATION_ALT;
		
		if(User.selectedCharacter != null) {
			for(MMOCharacter character : characters) {
				if(character.id != null && character.id == User.selectedCharacter.id) {
					this.preSelected = character;
					break;
				}
			}
		}
	}

	@Override
	public void onSelected(MMOCharacter selected) {
		if(selected.id == null) throw new IllegalStateException("Character id is null");
		
		JsonObject body = new JsonObject();
		body.addProperty("characterId", selected.id);
		
		LCLPNetwork.post("api/ls5/set-active-character", body, response -> {
			if(response.isNoConnection()) {
				SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP, 
						new TranslationTextComponent("mmo.no_internet"),
						null);
				return;
			}
			else if(response.getResponseCode() == 200) {
				User.selectedCharacter = selected;
				Enqueuer.enqueueOnRender(() -> this.minecraft.displayGuiScreen(new MMOMainScreen(false)));
			} else {
				ITextComponent reason = new TranslationTextComponent("error.unknown");
				System.err.println(response);
				if(response.hasValidationViolations()) {
					reason = new StringTextComponent(response.getValidationViolations().getFirst());
				}

				SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
						reason,
						null);
			}
		});
	}

	@Override
	public List<MMOCharacter> getEntries() {
		return characters;
	}

	@Override
	public void addEntry() {
		this.minecraft.displayGuiScreen(new CharacterCreatorScreen(this));
	}

	@Override
	public void editEntry(MMOCharacter character) {
		this.minecraft.displayGuiScreen(new EditCharacterScreen(character, this));
	}

	@Override
	public void deleteEntry(MMOCharacter character) {
		if(character.id == null) throw new NullPointerException("Character id is null.");

		this.minecraft.displayGuiScreen(new ConfirmScreen(yes -> {
			if(yes) {
				JsonObject body = new JsonObject();
				body.addProperty("characterId", character.id);
				LCLPNetwork.sendRequest("api/ls5/delete-character", "DELETE", body, response -> {
					if(response.isNoConnection()) {
						SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
								new TranslationTextComponent("mmo.menu.select_character.delete_failed"),
								new TranslationTextComponent("mmo.no_internet"));
					} else if(response.getResponseCode() == 200) {
						SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
								new TranslationTextComponent("mmo.menu.select_character.delete_success"),
								null);
					} else {
						ITextComponent reason = new TranslationTextComponent("error.unknown");
						System.err.println(response);
						if(response.hasValidationViolations()) {
							reason = new StringTextComponent(response.getValidationViolations().getFirst());
						}

						SystemToast.addOrUpdate(this.minecraft.getToastGui(), SystemToast.Type.WORLD_BACKUP,
								new TranslationTextComponent("mmo.menu.select_character.delete_failed"),
								reason);
					}

					CharacterChooserScreen.updateContentAndShow(minecraft, prevScreen);
				});
			} else {
				this.minecraft.displayGuiScreen(CharacterChooserScreen.this);
			}
		}, 
				new TranslationTextComponent("mmo.menu.select_character.confirm_delete"), 
				new TranslationTextComponent("mmo.menu.select_character.confirm_delete_desc", character.getTitle().getFormattedText())));
	}

	@Override
	public void copyEntry(MMOCharacter character) {

	}

	public static void updateContentAndShow(final Minecraft mc, Screen prevScreen) {
		final Consumer<List<MMOCharacter>> callback = characters -> {
			final CharacterChooserScreen guiScreenIn = new CharacterChooserScreen(prevScreen, characters);

			Enqueuer.enqueueOnRender(() -> mc.displayGuiScreen(guiScreenIn));
		};

		LCLPNetwork.post("api/ls5/get-characters", null, response -> {
			if(response.isNoConnection() || response.getResponseCode() != 200) {
				callback.accept(new ArrayList<>());
				return;
			}

			List<MMOCharacter> characters = new ArrayList<>();

			JsonArray arr = JsonSerializeable.parse(response.getRawResponse(), JsonArray.class);
			arr.forEach(e -> {
				if(e.isJsonObject()) 
					characters.add(JsonSerializeable.cast(e, MMOCharacter.class));
			});

			callback.accept(characters);
		});
	}

}
