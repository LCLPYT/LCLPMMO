package work.lclpnet.mmo.asm.mixin.geckolib;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.example.GeckoLibMod;

@Mixin(GeckoLibMod.class)
public class MixinGeckoLibMod {

    @Shadow public static boolean DISABLE_IN_DEV;

    @Inject(
            method = "onInitialize",
            at = @At("HEAD")
    )
    public void disableInDev(CallbackInfo ci) {
        DISABLE_IN_DEV = true;
    }
}
