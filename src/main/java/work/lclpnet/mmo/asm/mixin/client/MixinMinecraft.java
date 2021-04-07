package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.audio.MMOBackgroundMusicSelector;
import work.lclpnet.mmo.audio.MMOBackgroundMusicTracks;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.gui.main.FakeWorld;

@Mixin(Minecraft.class)
@OnlyIn(Dist.CLIENT)
public class MixinMinecraft {

    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    public ClientWorld world;
    @Shadow
    @Final
    private MusicTicker musicTicker;

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onInitEnd(CallbackInfo callbackInfo) {
        System.out.printf("Mixins are enabled for '%s'. (CLIENT)\n", LCLPMMO.MODID);
    }

    @Inject(
            method = "getBackgroundMusicSelector()Lnet/minecraft/client/audio/BackgroundMusicSelector;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onGetBackgroundMusicSelector(CallbackInfoReturnable<BackgroundMusicSelector> cir) {
        SoundEvent enqueuedBackgroundMusic = MusicSystem.getEnqueuedBackgroundMusic();
        if (enqueuedBackgroundMusic != null) {
            MusicSystem.playBackgroundMusic(null);
            musicTicker.stop();
            musicTicker.timeUntilNextMusic = 0;
            cir.setReturnValue(new MMOBackgroundMusicSelector(enqueuedBackgroundMusic, 12000, 24000, true));
            cir.cancel();
            return;
        }

        if (this.world == null || this.world instanceof FakeWorld) { // while not joined a world
            cir.setReturnValue(MMOBackgroundMusicTracks.MAIN_MENU_MUSIC);
            cir.cancel();
        }
    }

}
