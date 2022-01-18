package work.lclpnet.mmo.client.render.entity.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.PixieEntity;

public class PixieModel extends AnimatedGeoModel<PixieEntity> {

    @Override
    public Identifier getModelLocation(PixieEntity object) {
        return LCLPMMO.identifier("geo/pixie.geo.json");
    }

    @Override
    public Identifier getTextureLocation(PixieEntity object) {
        return LCLPMMO.identifier("textures/entity/pixie.png");
    }

    @Override
    public Identifier getAnimationFileLocation(PixieEntity animatable) {
        return LCLPMMO.identifier("animations/pixie.animation.json");
    }
}
