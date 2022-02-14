package work.lclpnet.mmo.client.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordIntegration {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String applicationId = "796425447456899112";

    public static void init() {
        LOGGER.info("Initializing Discord Rich Presence...");

        Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC.INSTANCE::Discord_Shutdown));

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = user -> LOGGER.info("Discord is now ready.");
        handlers.disconnected = (errorCode, message) -> LOGGER.error("Discord disconnected (code {}): {}", errorCode, message);
        handlers.errored = (errorCode, message) -> LOGGER.error("Could not connect to discord (code {}): {}", errorCode, message);

        DiscordRPC.INSTANCE.Discord_Initialize(applicationId, handlers, true, "");

        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        presence.details = "In Menu";

        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);

        new Thread(DiscordIntegration::runCallbackHandlerLoop, "RPC-Callback-Handler").start();
    }

    @SuppressWarnings("BusyWait")
    public static void runCallbackHandlerLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            DiscordRPC.INSTANCE.Discord_RunCallbacks();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                DiscordRPC.INSTANCE.Discord_Shutdown();
                break;
            }
        }
    }
}
