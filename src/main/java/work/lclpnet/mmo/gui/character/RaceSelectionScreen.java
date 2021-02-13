package work.lclpnet.mmo.gui.character;

import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.gui.GenericSelectionScreen;

import java.util.ArrayList;
import java.util.List;

public class RaceSelectionScreen extends GenericSelectionScreen<MMORace>{

	private List<MMORace> entries;
	
	public RaceSelectionScreen(CharacterCreatorScreen prevScreen) {
		super(new TranslationTextComponent("mmo.menu.select_race.title"), prevScreen);
		this.background = BACKGROUND_LOCATION_ALT;
		this.preSelected = prevScreen.selectedRace;
		
		this.entries = new ArrayList<>(Races.getRaces());
	}

	@Override
	public List<MMORace> getEntries() {
		return entries;
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
