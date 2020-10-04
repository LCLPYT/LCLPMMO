package work.lclpnet.mmo.facade.race;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class RaceHuman extends MMORace{

	RaceHuman() {
		super("human", new TranslationTextComponent("mmo.race.human.title"));
	}
	
	@Override
	public ResourceLocation getIcon() {
		return new ResourceLocation("textures/item/iron_leggings.png");
	}

}
