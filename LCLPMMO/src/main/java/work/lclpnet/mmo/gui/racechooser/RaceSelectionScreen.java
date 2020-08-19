package work.lclpnet.mmo.gui.racechooser;

import java.util.List;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.Race;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.gui.GenericSelectionScreen;

public class RaceSelectionScreen extends GenericSelectionScreen<Race>{

	public RaceSelectionScreen(Screen screenIn) {
		super(new TranslationTextComponent("mmo.menu.select_race.title"), screenIn);
	}

	@Override
	public List<Race> getEntries() {
		return Races.getRaces();
	}

	@Override
	public void onSelected(Race selected) {
		System.out.printf("SELECTED: %s\n", selected);
	}

}
