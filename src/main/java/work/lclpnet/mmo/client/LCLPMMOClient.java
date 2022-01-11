package work.lclpnet.mmo.client;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ClientModInitializer;
import work.lclpnet.mmo.client.module.DecorationsClientModule;
import work.lclpnet.mmo.client.module.IClientModule;
import work.lclpnet.mmo.client.module.PixieClientModule;
import work.lclpnet.mmo.network.LMNetworking;

import java.util.Set;

public class LCLPMMOClient implements ClientModInitializer {

    private static Set<IClientModule> modules = ImmutableSet.of(
            new DecorationsClientModule(),
            new PixieClientModule()
    );

    @Override
    public void onInitializeClient() {
        LMNetworking.registerPackets();
        LMNetworking.registerClientPacketHandlers();

        modules.forEach(IClientModule::register);
        modules = null;
    }
}
