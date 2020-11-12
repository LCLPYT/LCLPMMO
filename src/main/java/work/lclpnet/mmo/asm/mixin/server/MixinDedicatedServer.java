package work.lclpnet.mmo.asm.mixin.server;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.dedicated.DedicatedServer;
import work.lclpnet.mmo.LCLPMMO;

@Mixin(DedicatedServer.class)
public class MixinDedicatedServer {
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	public void onInitEnd(CallbackInfo callbackInfo) {
		System.out.printf("Mixins are enabled for '%s'. (DEDICATED SERVER)\n", LCLPMMO.MODID);
	}
	
}
