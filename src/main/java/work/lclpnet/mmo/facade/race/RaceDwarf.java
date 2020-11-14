package work.lclpnet.mmo.facade.race;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RaceDwarf extends MMORace{

	RaceDwarf() {
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