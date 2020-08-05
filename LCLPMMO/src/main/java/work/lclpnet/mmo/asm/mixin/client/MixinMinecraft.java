package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.LCLPMMO;

@Mixin(Minecraft.class)
@OnlyIn(Dist.CLIENT)
public class MixinMinecraft {

	@Inject(method = "<init>*", at = @At("RETURN"))
	public void onInitEnd(CallbackInfo callbackInfo) {
		System.out.printf("Mixins are enabled for '%s'. (CLIENT)\n", LCLPMMO.MODID);
	}
	
}
