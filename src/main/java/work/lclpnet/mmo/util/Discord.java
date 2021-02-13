package work.lclpnet.mmo.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Discord {

	private static final Logger LOGGER = LogManager.getLogger();
	
	@SuppressWarnings("BusyWait")
	public static void initRPC() {
		LOGGER.info("Initializing Discord Rich Presence...");
		
		Runtime.getRuntime().addShutdownHook(new Thread(DiscordRPC.INSTANCE::Discord_Shutdown));
		
		DiscordRPC lib = DiscordRPC.INSTANCE;
		
		DiscordEventHandlers handlers = new DiscordEventHandlers();
		handlers.ready = user -> System.out.println("ready");

		final String applicationId = "796425447456899112";
		final String steamId = "";
		
		lib.Discord_Initialize(applicationId, handlers, true, steamId);
		
		DiscordRichPresence presence = new DiscordRichPresence();
		presence.startTimestamp = System.currentTimeMillis() / 1000;
		presence.details = "In Menu";
		
		lib.Discord_UpdatePresence(presence);
		
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				lib.Discord_RunCallbacks();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					lib.Discord_Shutdown();
					break;
				}
			}
		}, "RPC-Callback-Handler").start();
	}
	
}
