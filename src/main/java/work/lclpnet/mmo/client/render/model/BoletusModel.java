package work.lclpnet.mmo.client.render.model;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.BoletusEntity;

import javax.annotation.Nullable;

public class BoletusModel extends AnimatedGeoModel<BoletusEntity> {

    @Override
    public ResourceLocation getModelLocation(BoletusEntity object) {
        return new ResourceLocation(LCLPMMO.MODID, "geo/boletus.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(BoletusEntity object) {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/boletus.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(BoletusEntity object) {
        return new ResourceLocation(LCLPMMO.MODID, "animations/boletus.animation.json");
    }

    @Override
    public void setLivingAnimations(BoletusEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        if (customPredicate == null) return;
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationProcessor<?> ap = this.getAnimationProcessor();
        IBone head = ap.getBone("head");
        IBone leftLeg = ap.getBone("left_leg");
        IBone rightLeg = ap.getBone("right_leg");

        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));

        float limbSwingAmount = customPredicate.getLimbSwingAmount();
        float limbSwing = customPredicate.getLimbSwing();

        float modLimbSwingAmount = limbSwingAmount > 0.2F ? limbSwingAmount * 0.4F : limbSwingAmount;

        rightLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F) * 1.4F * modLimbSwingAmount);
        leftLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * modLimbSwingAmount);
    }
}
