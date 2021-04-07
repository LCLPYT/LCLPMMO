package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.audio.MMOBackgroundMusicSelector;
import work.lclpnet.mmo.audio.MusicSystem;

@Mixin(MusicTicker.class)
public class MixinMusicTicker {

    @Final
    @Shadow
    private Minecraft client;
    @Shadow
    public int timeUntilNextMusic;
    @Shadow
    private ISound currentMusic;

    @Inject(
            method = "Lnet/minecraft/client/audio/MusicTicker;selectRandomBackgroundMusic("
                    + "Lnet/minecraft/client/audio/BackgroundMusicSelector;"
                    + ")V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onSelectRandomBackgroundMusic(BackgroundMusicSelector sel, CallbackInfo ci) {
        if (Config.isMinecraftMusicDisabled() && !(sel instanceof MMOBackgroundMusicSelector))
            ci.cancel();
    }

    @Inject(
            method = "tick()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/audio/MusicTicker;currentMusic:Lnet/minecraft/client/audio/ISound;",
                    opcode = Opcodes.PUTFIELD
            ),
            cancellable = true
    )
    public void onSoundStopped(CallbackInfo ci) {
        if (MusicSystem.isLoopBackgroundMusic()
                && currentMusic != null
                && MusicSystem.getLastBackgroundMusic() != null
                && MusicSystem.getLastBackgroundMusic().name.equals(currentMusic.getSoundLocation())) {

            if (this.currentMusic.getSound() != SoundHandler.MISSING_SOUND) {
                this.client.getSoundHandler().play(this.currentMusic);
            }

            this.timeUntilNextMusic = Integer.MAX_VALUE;
            ci.cancel();
        }
    }

}
