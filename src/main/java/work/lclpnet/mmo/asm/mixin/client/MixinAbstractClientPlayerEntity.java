package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.LCLPMMO;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {

    private static final ResourceLocation MMO_DEBUG_CAPE = new ResourceLocation(LCLPMMO.MODID, "textures/entity/cape.png");

    @Inject(
            method = "getLocationCape()Lnet/minecraft/util/ResourceLocation;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetLocationCape(CallbackInfoReturnable<ResourceLocation> cir) {
        if (Config.isDebugCape()) {
            cir.setReturnValue(MMO_DEBUG_CAPE);
            cir.cancel();
        }
    }
}
