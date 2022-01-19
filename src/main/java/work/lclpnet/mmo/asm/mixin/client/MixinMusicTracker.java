package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.MusicSound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.client.audio.MMOMusic;
import work.lclpnet.mmo.client.audio.MMOMusicSound;
import work.lclpnet.mmo.client.gui.main.FakeClientWorld;
import work.lclpnet.mmocontent.asm.mixin.common.SoundEventAccessor;

@Mixin(MusicTracker.class)
public class MixinMusicTracker {

    @Shadow private @Nullable SoundInstance current;

    @Shadow @Final private MinecraftClient client;

    @Shadow private int timeUntilNextSong;

    @Inject(
            method = "play(Lnet/minecraft/sound/MusicSound;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onPlay(MusicSound type, CallbackInfo ci) {
        if (type instanceof MMOMusicSound) return;

        if (Config.isMinecraftMusicDisabled() || client.world == null || client.world instanceof FakeClientWorld)
            ci.cancel();
    }

    @Inject(
            method = "tick()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/sound/MusicTracker;current:Lnet/minecraft/client/sound/SoundInstance;",
                    opcode = Opcodes.PUTFIELD
            ),
            cancellable = true
    )
    public void onSoundStopped(CallbackInfo ci) {
        if (!MMOMusic.shouldLoop() || current == null || MMOMusic.getLastMusic() == null) return;

        Identifier id = ((SoundEventAccessor) MMOMusic.getLastMusic()).getId();
        if (!id.equals(current.getId())) return;

        if (this.current.getSound() != SoundManager.MISSING_SOUND)
            this.client.getSoundManager().play(this.current);

        this.timeUntilNextSong = Integer.MAX_VALUE;
        ci.cancel();
    }
}
