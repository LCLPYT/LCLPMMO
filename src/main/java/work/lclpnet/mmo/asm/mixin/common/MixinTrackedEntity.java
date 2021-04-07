package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.TrackedEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.entity.ILimitTracking;

@Mixin(TrackedEntity.class)
public class MixinTrackedEntity {

    @Shadow
    @Final
    private Entity trackedEntity;

    @Inject(
            method = "track(Lnet/minecraft/entity/player/ServerPlayerEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onTrack(ServerPlayerEntity p, CallbackInfo ci) {
        if (trackedEntity instanceof ILimitTracking && !((ILimitTracking) trackedEntity).shouldBeTrackedBy(p))
            ci.cancel();
    }

}
