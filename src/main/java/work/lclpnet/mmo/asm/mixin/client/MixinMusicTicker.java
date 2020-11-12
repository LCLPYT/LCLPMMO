package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.MusicTicker;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.audio.MMOBackgroundMusicSelector;

@Mixin(MusicTicker.class)
public class MixinMusicTicker {

	@Inject(
			method = "Lnet/minecraft/client/audio/MusicTicker;selectRandomBackgroundMusic("
					+ "Lnet/minecraft/client/audio/BackgroundMusicSelector;"
					+ ")V",
					at = @At("HEAD"),
					cancellable = true
			)
	public void onSelectRandomBackgroundMusic(BackgroundMusicSelector sel, CallbackInfo ci) {
		if(Config.isMinecraftMusicDisabled() && !(sel instanceof MMOBackgroundMusicSelector))
			ci.cancel();
	}
	
}
