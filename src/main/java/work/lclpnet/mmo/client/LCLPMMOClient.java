package work.lclpnet.mmo.client;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ClientModInitializer;
import work.lclpnet.mmo.client.module.*;
import work.lclpnet.mmo.client.util.UpdateChecker;
import work.lclpnet.mmo.network.LMNetworking;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import java.util.Set;

public class LCLPMMOClient implements ClientModInitializer {

    private static Set<IClientModule> modules = ImmutableSet.of(
            new DecorationsClientModule(),
            new PixieClientModule(),
            new BoletusClientModule(),
            new TitleScreenClientModule(),
            new MusicClientModule()
    );

    @Override
    public void onInitializeClient() {
        UpdateChecker.checkForUpdates();

        LMNetworking.registerPackets();
        LMNetworking.registerClientPacketHandlers();

        modules.forEach(IClientModule::register);
        modules = null;

        LCLPNetworkSession.startup();
    }
}
