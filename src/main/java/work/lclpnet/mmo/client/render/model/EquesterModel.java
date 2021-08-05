package work.lclpnet.mmo.client.render.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.EquesterEntity;

public class EquesterModel extends AnimatedGeoModel<EquesterEntity> {

    @Override
    public ResourceLocation getModelLocation(EquesterEntity equesterEntity) {
        return new ResourceLocation(LCLPMMO.MODID, "geo/equester.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(EquesterEntity equesterEntity) {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/equester.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(EquesterEntity equesterEntity) {
        return new ResourceLocation(LCLPMMO.MODID, "animations/fallen_knight.animation.json");
    }

    /*@Override
    public void setLivingAnimations(FallenKnightEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        if(customPredicate == null) return;
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);

        AnimationProcessor<?> ap = this.getAnimationProcessor();
        IBone head = ap.getBone("head");
        IBone leftLeg = ap.getBone("left_leg");
        IBone rightLeg = ap.getBone("right_leg");

        head.setRotationY(extraData.netHeadYaw * ((float)Math.PI / 180F));
        head.setRotationX(extraData.headPitch * ((float)Math.PI / 180F));

        float limbSwingAmount = customPredicate.getLimbSwingAmount();
        float limbSwing = customPredicate.getLimbSwing();

        float modLimbSwingAmount = limbSwingAmount > 0.2F ? limbSwingAmount * 0.4F : limbSwingAmount;

        rightLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F) * 1.4F * modLimbSwingAmount);
        leftLeg.setRotationX(MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * modLimbSwingAmount);
    }
     */
}
