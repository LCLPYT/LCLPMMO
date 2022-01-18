package work.lclpnet.mmo.client.render.entity.model;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.BoletusEntity;

public class BoletusModel extends AnimatedGeoModel<BoletusEntity> {

    @Override
    public Identifier getModelLocation(BoletusEntity object) {
        return LCLPMMO.identifier("geo/boletus.geo.json");
    }

    @Override
    public Identifier getTextureLocation(BoletusEntity object) {
        return LCLPMMO.identifier("textures/entity/boletus.png");
    }

    @Override
    public Identifier getAnimationFileLocation(BoletusEntity animatable) {
        return LCLPMMO.identifier("animations/boletus.animation.json");
    }

    @Override
    public void setLivingAnimations(BoletusEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        if (customPredicate == null) return;

        @SuppressWarnings("unchecked")
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationProcessor<?> processor = this.getAnimationProcessor();
        IBone head = processor.getBone("head");
        IBone leftLeg = processor.getBone("left_leg");
        IBone rightLeg = processor.getBone("right_leg");

        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));

        float limbSwingAmount = customPredicate.getLimbSwingAmount();
        float limbSwing = customPredicate.getLimbSwing();

        float modLimbSwingAmount = limbSwingAmount > 0.2F ? limbSwingAmount * 0.4F : limbSwingAmount;

        rightLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F) * 1.4F * modLimbSwingAmount);
        leftLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * modLimbSwingAmount);
    }
}
