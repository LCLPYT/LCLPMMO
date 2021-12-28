package work.lclpnet.mmo.client;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ClientModInitializer;
import work.lclpnet.mmo.client.module.DecorationsClientModule;
import work.lclpnet.mmo.client.module.IClientModule;

import java.util.Set;

public class LCLPMMOClient implements ClientModInitializer {

    private static Set<IClientModule> modules = ImmutableSet.of(
            new DecorationsClientModule()
    );

    @Override
    public void onInitializeClient() {
        modules.forEach(IClientModule::register);
        modules = null;
    }
}
