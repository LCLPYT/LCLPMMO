package work.lclpnet.mmo.client.module;

import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.client.util.DiscordIntegration;

public class DiscordClientModule implements IClientModule {

    @Override
    public void register() {
        if (Config.isDiscordIntegrationEnabled()) DiscordIntegration.init();
    }
}
