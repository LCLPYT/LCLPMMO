package work.lclpnet.mmo.facade.race.humanoid;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.facade.race.MMORace;

public class RaceHuman extends MMORace {

    public RaceHuman() {
        super("human", new TranslationTextComponent("mmo.race.human.title"));
    }

    @Override
    public ResourceLocation getIcon() {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/human/icon.png");
    }
}
