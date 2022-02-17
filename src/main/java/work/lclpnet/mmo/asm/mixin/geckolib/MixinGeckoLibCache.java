package work.lclpnet.mmo.asm.mixin.geckolib;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.resource.GeckoLibCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(GeckoLibCache.class)
public class MixinGeckoLibCache {

    // Prevents the loading of any geckolib dev examples resources (not needed, and sometimes produce exceptions)
    @Inject(
            method = "lambda$loadResources$6",  // may change when updating versions
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Collection;iterator()Ljava/util/Iterator;"
            ),
            remap = false
    )
    private static void preventGeckoLibResourceLoad(Function<?, ?> existing, Executor executor, Collection<Identifier> resources, CallbackInfoReturnable<Map<?, ?>> cir) {
        resources.removeIf(r -> r.getNamespace().equals(GeckoLib.ModID));
    }
}
