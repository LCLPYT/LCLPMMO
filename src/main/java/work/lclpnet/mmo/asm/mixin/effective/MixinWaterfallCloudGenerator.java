package work.lclpnet.mmo.asm.mixin.effective;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "ladysnake.effective.client.world.WaterfallCloudGenerators$WaterfallCloudGenerator")
public class MixinWaterfallCloudGenerator {

    @ModifyArg(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V"
            ),
            index = 5
    )
    private float fixVolume(float volume) {
        return 0.15F;
    }
}
