package work.lclpnet.mmo.data.race.humanoid;

import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.data.race.IMMORace;

public class RaceElven extends IMMORace {

    public RaceElven() {
        super("elven", new TranslatableText("mmo.race.elven.title"));
    }

    @Override
    public Identifier getIcon() {
        return LCLPMMO.identifier("textures/entity/elven/icon.png");
    }
}
