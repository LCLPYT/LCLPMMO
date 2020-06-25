package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import work.lclpnet.mmo.LCLPMMO;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Inject(method = "Lnet/minecraft/client/Minecraft;<init>(Lnet/minecraft/client/GameConfiguration;)V", at = @At("RETURN"))
	public void onInitEnd(CallbackInfo callbackInfo) {
		System.out.printf("Mixins are enabled for '%s'. (CLIENT)\n", LCLPMMO.MODID);
	}
	
}
