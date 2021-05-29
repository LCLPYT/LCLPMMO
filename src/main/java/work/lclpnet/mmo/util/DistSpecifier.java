package work.lclpnet.mmo.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Arrays;

public enum DistSpecifier {

    NONE,
    CLIENT(Dist.CLIENT),
    ALL(Dist.CLIENT, Dist.DEDICATED_SERVER);

    private final Dist[] matches;

    DistSpecifier(Dist... matches) {
        this.matches = matches;
    }

    public boolean isApplicable() {
        if (this.equals(DistSpecifier.NONE)) return false;
        else if (FMLEnvironment.dist == null) return true;
        else return Arrays.asList(this.matches).contains(FMLEnvironment.dist);
    }
}
