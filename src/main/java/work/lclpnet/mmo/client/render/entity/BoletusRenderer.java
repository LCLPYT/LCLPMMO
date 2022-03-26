package work.lclpnet.mmo.client.render.entity;

import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import work.lclpnet.mmo.client.render.entity.model.BoletusModel;
import work.lclpnet.mmo.entity.BoletusEntity;

public class BoletusRenderer extends GeoEntityRenderer<BoletusEntity> {

    public BoletusRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new BoletusModel());
        this.shadowRadius = 0.7F;
    }
}
