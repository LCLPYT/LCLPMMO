package work.lclpnet.mmo.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.model.EquesterModel;
import work.lclpnet.mmo.entity.EquesterEntity;

public class EquesterRenderer extends GeoEntityRenderer<EquesterEntity> {

    protected EquesterRenderer(EntityRendererManager renderManager) {
        super(renderManager, new EquesterModel());
        this.shadowSize = 0.7F;
    }
}
