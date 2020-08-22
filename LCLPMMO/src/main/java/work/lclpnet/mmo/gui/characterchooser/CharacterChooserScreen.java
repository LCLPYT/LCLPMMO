package work.lclpnet.mmo.gui.characterchooser;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.character.Characters;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.EditableGenericSelectionScreen;
import work.lclpnet.mmo.gui.charactercreator.CharacterCreatorScreen;

public class CharacterChooserScreen extends EditableGenericSelectionScreen<MMOCharacter> {

	public CharacterChooserScreen(Screen prevScreen) {
		super(new TranslationTextComponent("mmo.menu.select_character.title"), prevScreen);
	}

	@Override
	public void onSelected(MMOCharacter selected) {
		System.out.println("SELECTED " + selected.toString());
	}

	@Override
	public List<MMOCharacter> getEntries() {
		return Characters.getCharacters();
	}
	
	@Override
	protected void init() {
		super.init();
		editButton.active = false;
		deleteButton.active = false;
		copyButton.active = false;
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
		Characters.getCharacters().remove(character);
		
	}

	@Override
	public void copyEntry(MMOCharacter character) {
		
	}
	
}
