package work.lclpnet.mmo.client.module;

import work.lclpnet.mmo.client.render.entity.PixieRenderer;
import work.lclpnet.mmo.module.PixieModule;

import static work.lclpnet.mmo.client.util.MMOClientHelper.registerEntityRenderer;

public class PixieClientModule implements IClientModule {

    @Override
    public void register() {
        registerEntityRenderer(PixieModule.pixieEntityType, PixieRenderer::new);
    }
}
