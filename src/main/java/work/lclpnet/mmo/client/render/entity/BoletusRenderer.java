package work.lclpnet.mmo.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.entity.model.BoletusModel;
import work.lclpnet.mmo.entity.BoletusEntity;

public class BoletusRenderer extends GeoEntityRenderer<BoletusEntity> {

    public BoletusRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BoletusModel());
        this.shadowRadius = 0.7F;
    }
}
