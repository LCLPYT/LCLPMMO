package work.lclpnet.mmo.client.module;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.asm.mixin.client.TitleScreenAccessor;
import work.lclpnet.mmo.client.event.MMOFirstScreenOpenCallback;
import work.lclpnet.mmo.client.event.ScreenOpenCallback;
import work.lclpnet.mmo.client.gui.login.LoginScreen;
import work.lclpnet.mmo.client.gui.main.MMOTitleScreen;
import work.lclpnet.mmo.client.gui.main.PreIntroScreen;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

public class TitleScreenClientModule implements IClientModule {

    @Override
    public void register() {
        ScreenOpenCallback.EVENT.register(screen -> {
            if (screen instanceof TitleScreen) {
                // fade will only be true, if it is the first call after the game has loaded
                boolean fade = ((TitleScreenAccessor) screen).doBackgroundFade();

                if (fade) {
                    // first startup
                    boolean cancel = MMOFirstScreenOpenCallback.EVENT.invoker().beforeFirstScreenOpen();
                    if (cancel) return null;

                    return LCLPNetworkSession.isLoggedIn() ? getStartScreen() : new LoginScreen();
                }

                return new MMOTitleScreen(false);
            }

            // open original screen
            return null;
        });
    }

    /**
     * Gets an instance of the starting screen (the screen that appears at normal startup; after any logins, agreements etc.)
     * @return An instance of the starting screen.
     */
    public static Screen getStartScreen() {
        return Config.shouldSkipIntro() ? new MMOTitleScreen(true) : new PreIntroScreen();
    }
}
