package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {

    @Accessor("OVERWORLD")
    static DimensionType getOverworld() {
        throw new AssertionError();
    }
}
