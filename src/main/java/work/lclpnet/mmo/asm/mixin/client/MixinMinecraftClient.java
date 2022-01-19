package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.client.audio.MMOMusic;
import work.lclpnet.mmo.client.audio.MMOMusicSound;
import work.lclpnet.mmo.client.event.ScreenOpenCallback;
import work.lclpnet.mmo.client.gui.main.FakeClientWorld;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Final private MusicTracker musicTracker;

    @ModifyVariable(
            method = "openScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    public Screen modifyOpenedScreen(Screen original) {
        Screen modified = ScreenOpenCallback.EVENT.invoker().onScreenOpen(original);
        return modified != null ? modified : original;
    }

    @Inject(
            method = "getMusicType()Lnet/minecraft/sound/MusicSound;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getMusicType(CallbackInfoReturnable<MusicSound> cir) {
        SoundEvent queued = MMOMusic.getQueuedMusic();
        if (queued != null) {
            MMOMusic.playBackgroundMusic(null);
            musicTracker.stop();
            ((MusicTrackerAccessor) musicTracker).setTimeUntilNextSong(0);
            cir.setReturnValue(new MMOMusicSound(queued, 12000, 24000, true));
            cir.cancel();
            return;
        }

        if (this.player != null && this.player.world instanceof FakeClientWorld)
            cir.setReturnValue(MMOMusic.MAIN_MENU_MUSIC);
    }
}
