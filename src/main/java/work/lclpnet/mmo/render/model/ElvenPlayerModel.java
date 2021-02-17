package work.lclpnet.mmo.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.render.MMOModelRenderer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ElvenPlayerModel extends AbstractMMOPlayerModel {

    private MMOModelRenderer leftEar;
    private MMOModelRenderer rightEar;

    public ElvenPlayerModel() {
        super(0F, true);

        textureWidth = 64;
        textureHeight = 64;

        populate();
    }

    private void populate() {
        bipedHead = new MMOModelRenderer(this);
        setMMOTranslation(bipedHead, 0F, -0.25F, 0F);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);

        bipedBody = new MMOModelRenderer(this);
        setMMOTranslation(bipedBody, 0F, -0.25F, 0F);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 14.0F, 4.0F, 0.0F, false);

        bipedLeftArm = new MMOModelRenderer(this);
        setMMOTranslation(bipedLeftArm, 0F, -0.25F, 0F);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedLeftArm.setTextureOffset(40, 16).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F, 0.0F, true);

        bipedLeftArmwear = new MMOModelRenderer(this);

        bipedRightArm = new MMOModelRenderer(this);
        setMMOTranslation(bipedRightArm, 0.0625F, -0.25F, 0F);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedRightArm.setTextureOffset(32, 46).addBox(-3.0F, -2.0F, -2.0F, 3.0F, 14.0F, 4.0F, 0.0F, true);

        bipedRightArmwear = new MMOModelRenderer(this);

        bipedLeftLeg = new MMOModelRenderer(this);
        setMMOTranslation(bipedLeftLeg, 0F, -0.125F, 0F);
        bipedLeftLeg.setRotationPoint(1.9F, 12F, 0.0F);
        bipedLeftLeg.setTextureOffset(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, false);

        bipedRightLeg = new MMOModelRenderer(this);
        setMMOTranslation(bipedRightLeg, 0F, -0.125F, 0F);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedRightLeg.setTextureOffset(16, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, 0.0F, true);

        leftEar = new MMOModelRenderer(this);
        leftEar.setRotationPoint(0.5F, -3.0F, 3.0F);
        bipedHead.addChild(leftEar);
        leftEar.setTextureOffset(34, 1).addBox(3.5F, -1.0F, -4.0F, 0.5F, 1.0F, 1.0F, 0.0F, false);
        leftEar.setTextureOffset(41, 2).addBox(3.5F, -1.5F, -3.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        leftEar.setTextureOffset(38, 2).addBox(3.5F, -1.5F, -3.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);
        leftEar.setTextureOffset(38, 5).addBox(3.5F, -0.5F, -3.0F, 0.5F, 1.0F, 0.5F, 0.0F, false);
        leftEar.setTextureOffset(44, 2).addBox(4.5F, -1.5F, -2.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);
        leftEar.setTextureOffset(43, 2).addBox(4.0F, -2.0F, -3.0F, 1.0F, 0.5F, 1.0F, 0.0F, false);
        leftEar.setTextureOffset(50, 3).addBox(4.5F, -2.5F, -2.0F, 0.5F, 1.0F, 0.5F, 0.0F, false);
        leftEar.setTextureOffset(50, 5).addBox(4.5F, -2.5F, -1.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

        rightEar = new MMOModelRenderer(this);
        rightEar.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.addChild(rightEar);
        rightEar.setTextureOffset(34, 1).addBox(-4.5F, -4.0F, -1.0F, 0.5F, 1.0F, 1.0F, 0.0F, false);
        rightEar.setTextureOffset(41, 8).addBox(-5.0F, -4.5F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        rightEar.setTextureOffset(38, 8).addBox(-4.5F, -4.5F, -0.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);
        rightEar.setTextureOffset(38, 11).addBox(-4.5F, -3.5F, 0.0F, 0.5F, 1.0F, 0.5F, 0.0F, false);
        rightEar.setTextureOffset(44, 8).addBox(-5.5F, -4.5F, 0.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);
        rightEar.setTextureOffset(43, 8).addBox(-5.5F, -5.0F, 0.0F, 1.0F, 0.5F, 1.0F, 0.0F, false);
        rightEar.setTextureOffset(50, 9).addBox(-5.5F, -5.5F, 1.0F, 0.5F, 1.0F, 0.5F, 0.0F, false);
        rightEar.setTextureOffset(50, 11).addBox(-5.5F, -5.5F, 1.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

        bipedBodyWear = new MMOModelRenderer(this);
        bipedBodyWear.setRotationPoint(0.0F, 24.0F, 0.0F);
        bipedBodyWear.setTextureOffset(13, 39).addBox(-4.0F, -4.0F, 2.0F, 1.0F, 1.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(0 , 37).addBox(-3.0F, -4.0F, 2.0F, 7.0F, 3.0F, 1.0F, 0.0F, false);
        bipedBodyWear.setTextureOffset(18, 37).addBox(-1.0F, -1.0F, 2.0F, 3.0F, 1.0F, 1.0F, 0.0F, true);
        bipedBodyWear.setTextureOffset(33, 23).addBox( 2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(34, 23).addBox(-2.0F, -1.0F, 2.0F, 1.0F, 2.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(29, 37).addBox(-1.0F, 0.0F, 2.0F, 1.0F, 2.0F, 1.0F, 0.0F, false);
        bipedBodyWear.setTextureOffset(36, 37).addBox( 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(33, 21).addBox(-1.0F, -4.0F, 3.0F, 2.0F, 2.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(34, 21).addBox( 1.0F, -4.0F, 3.0F, 1.0F, 1.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(44, 38).addBox(-1.0F, 2.0F, 2.0F, 2.0F, 1.0F, 0.5F, 0.0F, false);
        bipedBodyWear.setTextureOffset(45, 38).addBox( 0.0F, 3.0F, 2.0F, 1.0F, 1.0F, 0.5F, 0.0F, false);
    }

    @Override
    @Nonnull
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(bipedHead);
    }

    @Override
    @Nonnull
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(bipedLeftLeg, bipedRightLeg, bipedBody, bipedLeftArm, bipedRightArm, bipedBodyWear);
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return new ResourceLocation(LCLPMMO.MODID, "textures/entity/elven/elven.png");
    }

    @Override
    @ParametersAreNonnullByDefault
    public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
        ModelRenderer modelrenderer = this.getArmForSide(sideIn);
        float sign = sideIn == HandSide.RIGHT ? 1F : -1F;
        float translateX = 0.5F * sign;
        float translateY = -2.0F;

        modelrenderer.rotationPointX += translateX;
        modelrenderer.rotationPointY += translateY;
        modelrenderer.translateRotate(matrixStackIn);
        modelrenderer.rotationPointX -= translateX;
        modelrenderer.rotationPointY -= translateY;
    }

}
