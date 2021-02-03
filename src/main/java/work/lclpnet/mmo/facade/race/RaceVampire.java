package work.lclpnet.mmo.facade.race;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;

public class RaceVampire extends MMORace {

	RaceVampire() {
		super("vampire", new TranslationTextComponent("mmo.race.vampire.title"));
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/vampire/icon.png");
	}

}
