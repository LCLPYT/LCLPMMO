package work.lclpnet.mmo.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

public class LCLPMMOServer implements DedicatedServerModInitializer {

    private static MinecraftServer server;
    private static boolean shouldShutdown = false;

    @Override
    public void onInitializeServer() {
        LCLPNetworkSession.startup().thenAccept(success -> {
            if (!success) shutdown();
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            LCLPMMOServer.server = server;
            if (shouldShutdown) server.stop(false);
        });
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void shutdown() {
        if (server == null) shouldShutdown = true;
        else server.stop(false);
    }
}
