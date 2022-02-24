package work.lclpnet.mmo.asm.mixin.distanthorizons;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.client.gui.main.FakeClientWorld;

@Mixin(targets = {"com.seibel.lod.fabric.ClientProxy"})
public class MixinClientProxy {

    @Inject(
            method = "worldLoadEvent",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    public void onWorldLoadCompat(World level, CallbackInfo ci) {
        // if the level is a FakeClientWorld (MMOTitleScreen), cancel world loading
        if (level instanceof FakeClientWorld) {
            ci.cancel();
        }
    }

    @Inject(
            method = "worldUnloadEvent",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    public void onWorldUnloadCompat(World level, CallbackInfo ci) {
        // if the level is a FakeClientWorld (MMOTitleScreen), cancel world loading
        if (level instanceof FakeClientWorld) {
            ci.cancel();
        }
    }
}
