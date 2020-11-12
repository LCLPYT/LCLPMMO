package work.lclpnet.mmo.gui.character;

import java.util.List;

import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.gui.GenericSelectionScreen;

public class RaceSelectionScreen extends GenericSelectionScreen<MMORace>{

	public RaceSelectionScreen(CharacterCreatorScreen prevScreen) {
		super(new TranslationTextComponent("mmo.menu.select_race.title"), prevScreen);
		this.background = BACKGROUND_LOCATION_ALT;
		this.preSelected = prevScreen.selectedRace;
	}

	@Override
	public List<MMORace> getEntries() {
		return Races.getRaces();
	}

	@Override
	public void onSelected(MMORace selected) {
		CharacterCreatorScreen ccs = getCharacterCreatorScreen();
		ccs.setSelectedRace(selected);
		this.minecraft.displayGuiScreen(ccs);
	}
	
	public CharacterCreatorScreen getCharacterCreatorScreen() {
		return (CharacterCreatorScreen) this.prevScreen;
	}
	
}
