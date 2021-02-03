package work.lclpnet.mmo.facade.race;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;

public class RaceHuman extends MMORace{

	RaceHuman() {
		super("human", new TranslationTextComponent("mmo.race.human.title"));
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/human/icon.png");
	}

}
