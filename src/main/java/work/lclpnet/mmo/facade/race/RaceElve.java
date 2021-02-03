package work.lclpnet.mmo.facade.race;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;

public class RaceElve extends MMORace {

	RaceElve() {
		super("elve", new TranslationTextComponent("mmo.race.dwarf.title"));
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/elve/icon.png");
	}

}
