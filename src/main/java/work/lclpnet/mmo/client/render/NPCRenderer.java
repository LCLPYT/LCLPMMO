package work.lclpnet.mmo.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.model.NPCModel;

public class NPCRenderer extends GeoEntityRenderer {
    protected NPCRenderer(EntityRendererManager renderManager) {
        super(renderManager, new NPCModel());
        this.shadowSize = 0.7F;
    }
}
