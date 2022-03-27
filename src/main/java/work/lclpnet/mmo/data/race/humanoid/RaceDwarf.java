package work.lclpnet.mmo.data.race.humanoid;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.data.race.MMORace;

public class RaceDwarf extends MMORace {

    public RaceDwarf() {
        super("dwarf", new TranslatableText("mmo.race.dwarf.title"));
    }

    @Override
    public Identifier getIcon() {
        return new Identifier("textures/item/iron_pickaxe.png");
    }
	
	/*@Override
	public String getToolTip() {
		return new StringTextComponent("Dwarf").getFormattedText();
	}*/
}
