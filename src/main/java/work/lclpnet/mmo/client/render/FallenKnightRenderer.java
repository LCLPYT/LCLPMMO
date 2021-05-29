package work.lclpnet.mmo.client.render;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.model.BoletusModel;
import work.lclpnet.mmo.client.render.model.FallenKnightModel;
import work.lclpnet.mmo.entity.BoletusEntity;
import work.lclpnet.mmo.entity.FallenKnightEntity;

public class FallenKnightRenderer extends GeoEntityRenderer<FallenKnightEntity> {

    protected FallenKnightRenderer(EntityRendererManager renderManager) {
        super(renderManager, new FallenKnightModel());
        this.shadowSize = 0.7F;
    }

}
