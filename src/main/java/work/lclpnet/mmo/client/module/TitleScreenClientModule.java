package work.lclpnet.mmo.client.module;

import net.minecraft.client.gui.screen.TitleScreen;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.asm.mixin.client.TitleScreenAccessor;
import work.lclpnet.mmo.client.event.MMOFirstScreenOpenCallback;
import work.lclpnet.mmo.client.event.ScreenOpenCallback;
import work.lclpnet.mmo.client.gui.main.MMOTitleScreen;
import work.lclpnet.mmo.client.gui.main.PreIntroScreen;

public class TitleScreenClientModule implements IClientModule {

    @Override
    public void register() {
        ScreenOpenCallback.EVENT.register(screen -> {
            if (screen instanceof TitleScreen) {
                // fade will only be true, if it is the first call after the game has loaded
                boolean fade = ((TitleScreenAccessor) screen).doBackgroundFade();

                if (fade) {
                    boolean cancel = MMOFirstScreenOpenCallback.EVENT.invoker().beforeFirstScreenOpen();
                    if (cancel) return null;

                    if (!Config.shouldSkipIntro()) return new PreIntroScreen();
                }

                return new MMOTitleScreen(false);
            }

            // open original screen
            return null;
        });
    }
}
