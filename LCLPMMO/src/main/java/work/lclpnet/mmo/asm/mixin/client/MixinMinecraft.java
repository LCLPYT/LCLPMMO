package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.audio.MMOBackgroundMusicTracks;

@Mixin(Minecraft.class)
@OnlyIn(Dist.CLIENT)
public class MixinMinecraft {

	@Shadow
	public ClientPlayerEntity player;
	@Shadow
	public ClientWorld world;
	
	@Inject(method = "<init>*", at = @At("RETURN"))
	public void onInitEnd(CallbackInfo callbackInfo) {
		System.out.printf("Mixins are enabled for '%s'. (CLIENT)\n", LCLPMMO.MODID);
	}
	
	@Inject(
			method = "Lnet/minecraft/client/Minecraft;getBackgroundMusicSelector()Lnet/minecraft/client/audio/BackgroundMusicSelector;",
			at = @At("HEAD"),
			cancellable = true
			)
	public void onGetBackgroundMusicSelector(CallbackInfoReturnable<BackgroundMusicSelector> cir) {
		if(this.world == null) { // while not joined a world
			cir.setReturnValue(MMOBackgroundMusicTracks.MAIN_MENU_MUSIC);
			cir.cancel();
			return;
		}
	}
	
}
