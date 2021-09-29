package work.lclpnet.mmo.client.render.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import work.lclpnet.mmo.LCLPMMO;

public class NPCModel extends AnimatedGeoModel {
    @Override
    public ResourceLocation getModelLocation(Object o) {
        return new ResourceLocation(LCLPMMO.MODID, "geo/npc.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(Object o) {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/npc.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(Object o) {
        return new ResourceLocation(LCLPMMO.MODID, "animations/fallen_knight.animation.json");
    }

}
