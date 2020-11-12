package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.GameSettings;
import net.minecraft.util.math.MathHelper;

@Mixin(GameSettings.class)
public class MixinGameSettings {

	@Shadow
	public double gamma;
	
	@Inject(
			method = "Lnet/minecraft/client/GameSettings;loadOptions()V",
			at = @At("TAIL")
			)
	public void onLoadOptions(CallbackInfo ci) {
		System.out.println("GAMMA: " + gamma);
		this.gamma = MathHelper.clamp(this.gamma, 0D, 1D);
	}
	
}
