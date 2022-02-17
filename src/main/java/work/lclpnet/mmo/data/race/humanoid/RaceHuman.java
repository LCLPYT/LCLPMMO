package work.lclpnet.mmo.data.race.humanoid;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.data.race.MMORace;

public class RaceHuman extends MMORace {

    public RaceHuman() {
        super("human", new TranslatableText("mmo.race.human.title"));
    }

    @Override
    public Identifier getIcon() {
        return LCLPMMO.identifier("textures/entity/human/icon.png");
    }
}
