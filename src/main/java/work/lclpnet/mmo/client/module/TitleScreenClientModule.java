package work.lclpnet.mmo.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.asm.mixin.client.TitleScreenAccessor;
import work.lclpnet.mmo.client.MMOClient;
import work.lclpnet.mmo.client.event.MMOFirstScreenOpenCallback;
import work.lclpnet.mmo.client.event.ScreenOpenCallback;
import work.lclpnet.mmo.client.gui.character.CharacterChooserScreen;
import work.lclpnet.mmo.client.gui.character.CharacterCreatorScreen;
import work.lclpnet.mmo.client.gui.login.LoginScreen;
import work.lclpnet.mmo.client.gui.main.MMOTitleScreen;
import work.lclpnet.mmo.client.gui.main.PreIntroScreen;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

public class TitleScreenClientModule implements IClientModule {

    @Override
    public void register() {
        ScreenOpenCallback.EVENT.register(info -> {
            final Screen screen = info.getScreen();

            if (screen == null || screen instanceof TitleScreen) {
                // fade will only be true, if it is the first call after the game has loaded
                boolean fade = screen != null && ((TitleScreenAccessor) screen).doBackgroundFade();

                if (fade) {
                    // on startup
                    boolean cancel = MMOFirstScreenOpenCallback.EVENT.invoker().beforeFirstScreenOpen();
                    if (cancel) {
                        info.cancel();
                        return;
                    }

                    info.setScreen(LCLPNetworkSession.isLoggedIn() ? getStartScreen() : new LoginScreen());
                    return;
                }

                info.setScreen(new MMOTitleScreen(false));
            } else if (screen instanceof MultiplayerScreen || screen instanceof MultiplayerWarningScreen) {
                if (MMOClient.getActiveCharacter() == null) {
                    if (MMOClient.getCachedCharacters().isEmpty()) {
                        final MinecraftClient client = MinecraftClient.getInstance();
                        final Screen screenBefore = client.currentScreen;
                        info.setScreen(new CharacterCreatorScreen(success -> {
                            if (success) {
                                MMOClient.fetchAndCacheCharacters(true)
                                        .thenRun(() -> client.openScreen(screen));
                            } else {
                                if (screenBefore instanceof MMOTitleScreen) client.openScreen(screenBefore);
                                else client.openScreen(null);
                            }
                        }));
                    } else {
                        info.cancel();
                        CharacterChooserScreen.updateContentAndShow(MinecraftClient.getInstance(), screen, true);
                    }
                }
            }
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
