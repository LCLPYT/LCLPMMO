package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TitleScreen.class)
public interface TitleScreenAccessor {

    @Accessor("doBackgroundFade")
    boolean doBackgroundFade();
}
