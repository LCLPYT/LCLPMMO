package work.lclpnet.mmo.data.race.humanoid;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.data.race.IMMORace;

public class RaceDwarf extends IMMORace {

    public RaceDwarf() {
        super("dwarf", new TranslatableText("mmo.race.dwarf.title"));
    }

    @Override
    public Identifier getIcon() {
        return LCLPMMO.identifier("textures/item/iron_pickaxe.png");
    }
	
	/*@Override
	public String getToolTip() {
		return new StringTextComponent("Dwarf").getFormattedText();
	}*/
}