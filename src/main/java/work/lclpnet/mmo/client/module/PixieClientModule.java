package work.lclpnet.mmo.client.module;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import work.lclpnet.mmo.client.render.entity.PixieRenderer;
import work.lclpnet.mmo.module.PixieModule;

public class PixieClientModule implements IClientModule {

    @Override
    public void register() {
        EntityRendererRegistry.register(PixieModule.pixieEntityType, PixieRenderer::new);
    }
}
