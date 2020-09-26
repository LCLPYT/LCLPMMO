package work.lclpnet.mmo.gui.characterchooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.google.gson.JsonArray;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.EditableGenericSelectionScreen;
import work.lclpnet.mmo.gui.charactercreator.CharacterCreatorScreen;
import work.lclpnet.mmo.util.Enqueuer;
import work.lclpnet.mmo.util.LCLPNetwork;

public class CharacterChooserScreen extends EditableGenericSelectionScreen<MMOCharacter> {

	protected final List<MMOCharacter> characters;
	
	protected CharacterChooserScreen(Screen prevScreen, List<MMOCharacter> characters) {
		super(new TranslationTextComponent("mmo.menu.select_character.title"), prevScreen);
		this.characters = Objects.requireNonNull(characters);
	}

	@Override
	public void onSelected(MMOCharacter selected) {
		System.out.println("SELECTED " + selected.toString());
	}

	@Override
	public List<MMOCharacter> getEntries() {
		return characters;
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void addEntry() {
		this.minecraft.displayGuiScreen(new CharacterCreatorScreen(this));
	}

	@Override
	public void editEntry(MMOCharacter character) {

	}

	@Override
	public void deleteEntry(MMOCharacter character) {
		
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
