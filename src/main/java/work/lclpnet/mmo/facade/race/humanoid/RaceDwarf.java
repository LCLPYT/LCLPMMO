package work.lclpnet.mmo.facade.race.humanoid;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.MMORace;

public class RaceDwarf extends MMORace {

    public RaceDwarf() {
        super("dwarf", new TranslationTextComponent("mmo.race.dwarf.title"));
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation("textures/item/iron_pickaxe.png");
    }
	
	/*@Override
	public String getToolTip() {
		return new StringTextComponent("Dwarf").getFormattedText();
	}*/
}
