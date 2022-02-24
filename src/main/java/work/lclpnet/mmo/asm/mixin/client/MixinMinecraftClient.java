package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MusicTracker;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import work.lclpnet.mmo.client.audio.MMOMusic;
import work.lclpnet.mmo.client.audio.MMOMusicSound;
import work.lclpnet.mmo.client.event.LeaveWorldCallback;
import work.lclpnet.mmo.client.event.ScreenOpenCallback;
import work.lclpnet.mmo.client.gui.main.FakeClientWorld;
import work.lclpnet.mmo.client.gui.main.MMOTitleScreen;
import work.lclpnet.mmo.client.util.RenderWorker;

import java.util.Objects;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    private MusicTracker musicTracker;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    public abstract void openScreen(@Nullable Screen screen);

    @Inject(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;openScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void afterInitialTitleScreen(RunArgs args, CallbackInfo ci) {
        // title screen will be opened when the Splash screen starts up.
        // track the event to prevent MMOTitleScreen FakeWorld initialization for compat with Distant Horizons mod.
        MMOTitleScreen.setInitialTitleScreenShown();
    }

    @Inject(
            method = "openScreen",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onOpenScreen(Screen screen, CallbackInfo ci) {
        ScreenOpenCallback.Info info = new ScreenOpenCallback.Info(screen);
        ScreenOpenCallback.EVENT.invoker().onScreenOpen(info);

        if (info.isCancelled()) {
            ci.cancel();
            return;
        }

        Screen changed = info.getScreen();
        if (Objects.equals(screen, changed)) return;

        ci.cancel();
        openScreen(changed);
    }

    @Inject(
            method = "getMusicType()Lnet/minecraft/sound/MusicSound;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getMusicType(CallbackInfoReturnable<MusicSound> cir) {
        SoundEvent queued = MMOMusic.getQueuedMusic();
        if (queued != null) {
            MMOMusic.play(null);
            musicTracker.stop();
            ((MusicTrackerAccessor) musicTracker).setTimeUntilNextSong(0);
            cir.setReturnValue(new MMOMusicSound(queued, 12000, 24000, true));
            cir.cancel();
            return;
        }

        if (this.player != null && this.player.world instanceof FakeClientWorld)
            cir.setReturnValue(MMOMusic.MAIN_MENU_MUSIC);
    }

    @Inject(
            method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClientGame;onLeaveGameSession()V"
            )
    )
    public void fireLeaveWorldEventOnUnload(Screen screen, CallbackInfo ci) {
        LeaveWorldCallback.EVENT.invoker().leaveWorld(world);
    }

    @Inject(
            method = "joinWorld",
            at = @At("HEAD")
    )
    public void fireLeaveWorldEventOnLoad(ClientWorld world, CallbackInfo ci) {
        if (this.world != null) LeaveWorldCallback.EVENT.invoker().leaveWorld(this.world);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/ToastManager;draw(Lnet/minecraft/client/util/math/MatrixStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void afterRender(boolean tick, CallbackInfo ci) {
        RenderWorker.doWork();
    }
}
