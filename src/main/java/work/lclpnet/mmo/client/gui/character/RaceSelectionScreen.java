package work.lclpnet.mmo.client.gui.character;

import net.minecraft.text.TranslatableText;
import work.lclpnet.mmo.client.gui.select.GenericSelectionScreen;
import work.lclpnet.mmo.data.race.MMORace;
import work.lclpnet.mmo.data.race.Races;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RaceSelectionScreen extends GenericSelectionScreen<MMORace> {

    private final List<MMORace> entries;

    public RaceSelectionScreen(CharacterCreatorScreen previousScreen) {
        super(new TranslatableText("mmo.menu.select_race.title"), previousScreen);
        this.background = BACKGROUND_LOCATION_ALT;
        this.preSelected = previousScreen.selectedRace;

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

        Objects.requireNonNull(this.client).openScreen(ccs);
    }

    public CharacterCreatorScreen getCharacterCreatorScreen() {
        return (CharacterCreatorScreen) this.previousScreen;
    }
}
