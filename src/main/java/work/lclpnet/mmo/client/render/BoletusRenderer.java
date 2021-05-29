package work.lclpnet.mmo.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.model.BoletusModel;
import work.lclpnet.mmo.entity.BoletusEntity;

public class BoletusRenderer extends GeoEntityRenderer<BoletusEntity> {

    protected BoletusRenderer(EntityRendererManager renderManager) {
        super(renderManager, new BoletusModel());
        this.shadowSize = 0.7F;
    }
}
