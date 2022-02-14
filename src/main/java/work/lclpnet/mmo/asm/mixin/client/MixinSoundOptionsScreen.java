package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.gui.screen.options.SoundOptionsScreen;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.client.gui.widget.MMOCheckboxWidget;

@Mixin(SoundOptionsScreen.class)
public abstract class MixinSoundOptionsScreen {

    @Inject(
            method = "init",
            at = @At("TAIL")
    )
    public void addMMOMusicOnlyButton(CallbackInfo ci) {
        MMOCheckboxWidget checkbox = new MMOCheckboxWidget(10, 10, 150, 20, new TranslatableText("options_screen.audio.only_mmo"), Config.isMinecraftMusicDisabled());
        checkbox.setChangeListener(Config::setMinecraftMusicDisabled);
        ((ScreenAccessor) this).invokeAddButton(checkbox);
    }
}
