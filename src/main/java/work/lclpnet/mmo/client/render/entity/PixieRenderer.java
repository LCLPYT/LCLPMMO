package work.lclpnet.mmo.client.render.entity;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.client.render.entity.model.PixieModel;
import work.lclpnet.mmo.entity.PixieEntity;

public class PixieRenderer extends MobEntityRenderer<PixieEntity, PixieModel> {

    public PixieRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new PixieModel(), 0.15F);
    }

    @Override
    public Identifier getTexture(PixieEntity entity) {
        return LCLPMMO.identifier("textures/entity/pixie.png");
    }
}
