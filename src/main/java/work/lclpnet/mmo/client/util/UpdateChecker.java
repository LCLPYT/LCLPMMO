package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mmo.client.event.MMOFirstScreenOpenCallback;
import work.lclpnet.mmo.client.event.UpdateCheckCompleteEvent;
import work.lclpnet.mmo.client.gui.UpdateAvailableScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class UpdateChecker {

    private static boolean firstScreenOpened = false;
    private static final Logger logger = LogManager.getLogger();
    private static final AtomicBoolean updateRequired = new AtomicBoolean(false);

    public static boolean needsUpdate() {
        return updateRequired.get();
    }

    public static void checkForUpdates() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        // open update screen if there is an update on startup
        MMOFirstScreenOpenCallback.EVENT.register(() -> {
            firstScreenOpened = true;

            if (!needsUpdate()) return false;
            else {
                MinecraftClient.getInstance().setScreen(new UpdateAvailableScreen());
                return true;
            }
        });

        // in case the update check takes so long, that Minecraft has started already
        UpdateCheckCompleteEvent.EVENT.register(updateAvailable -> {
            if (updateAvailable && firstScreenOpened) {
                RenderWorker.push(() -> MinecraftClient.getInstance().setScreen(new UpdateAvailableScreen()));
            }
        });

        new Thread(UpdateChecker::check, "Update Checker").start();
    }

    private static void check() {
        logger.info("Checking for updates...");

        LCLPLauncher.getLCLPLauncherExecutable()
                .thenAcceptAsync(UpdateChecker::checkForUpdates)
                .exceptionally(ex -> {
                    logger.error("Could not check for updates", ex);
                    return null;
                });
    }

    private static void checkForUpdates(String program) {
        if (program == null) throw new IllegalStateException("LCLPLauncher executable could not be found.");

        try {
            final ProcessBuilder processBuilder = new ProcessBuilder(program, "check-for-app-update", "ls5")
                    .redirectErrorStream(true);

            logger.info("Executing '{}' ...", String.join(" ", processBuilder.command()));

            Process process = processBuilder.start();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    if ("[update-check]: Result -> NEEDS_UPDATE".equals(line)) {
                        updateRequired.set(true);
                        break;
                    }
                }
                process.waitFor();

                final boolean updateAvailable = updateRequired.get();
                logger.info(updateAvailable ? "An update is available." : "Already up-to-date.");
                UpdateCheckCompleteEvent.EVENT.invoker().onUpdateCheckComplete(updateAvailable);
            }
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
