package work.lclpnet.mmo.client.module;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.screen.PlayerScreenHandler;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.render.entity.BoletusRenderer;
import work.lclpnet.mmo.module.BoletusModule;
import work.lclpnet.mmo.particle.SporesParticle;

public class BoletusClientModule implements IClientModule {

    @Override
    public void register() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE)
                .register(((atlasTexture, registry) -> registry.register(LCLPMMO.identifier("particle/spores"))));

        ParticleFactoryRegistry.getInstance().register(BoletusModule.sporesParticleType, SporesParticle.Factory::new);

        EntityRendererRegistry.register(BoletusModule.boletusEntityType, BoletusRenderer::new);
    }
}
