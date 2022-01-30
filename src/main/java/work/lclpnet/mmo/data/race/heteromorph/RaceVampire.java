package work.lclpnet.mmo.data.race.heteromorph;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.data.race.IMMORace;

public class RaceVampire extends IMMORace {

    public RaceVampire() {
        super("vampire", new TranslatableText("mmo.race.vampire.title"));
    }

    @Override
    public Identifier getIcon() {
        return LCLPMMO.identifier("textures/entity/vampire/icon.png");
    }
}
