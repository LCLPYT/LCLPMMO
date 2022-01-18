package work.lclpnet.mmo.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.render.entity.model.BoletusModel;
import work.lclpnet.mmo.client.render.entity.model.PixieModel;
import work.lclpnet.mmo.entity.BoletusEntity;
import work.lclpnet.mmo.entity.PixieEntity;

public class BoletusRenderer extends GeoEntityRenderer<BoletusEntity> {

    public BoletusRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BoletusModel());
        this.shadowRadius = 0.7F;
    }
}
