package work.lclpnet.mmo.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.render.MMOModelRenderer;

public class VampirePlayerModel extends AbstractMMOPlayerModel {

	private MMOModelRenderer toothLeft;
	private MMOModelRenderer toothRight;
	private MMOModelRenderer earLeft;
	private MMOModelRenderer earRight;
	
	public VampirePlayerModel() {
		super(0F, false);

		textureWidth = 96;
		textureHeight = 64;
		
		populate();
	}

	@Override
	protected void populate() {
		this.loseModelReferences();

		bipedHead = new MMOModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		
		bipedHeadwear = new MMOModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.setTextureOffset(11, 6).addBox(-4.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(-2.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(1.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(3.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(16, 9).addBox(-4.0F, -7.0F, -4.5F, 8.0F, 2.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(20, 9).addBox(-3.0F, -8.0F, -4.5F, 6.0F, 1.0F, 0.5F, 0.0F, false);
		
		bipedBody = new MMOModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);
		
		bipedBodyWear = new MMOModelRenderer(this);
		bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBodyWear.setTextureOffset(40, 0).addBox(-2.0F, 1.0F, -2.5F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(56, 0).addBox(-2.0F, 2.0F, -2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(51, 0).addBox(1.0F, 2.0F, -2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(1, 37).addBox(2.0F, 0.0F, -2.5F, 2.0F, 12.0F, 0.5F, 0.0F, false);
		bipedBodyWear.setTextureOffset(65, 37).addBox(-4.0F, 0.0F, 2.0F, 8.0F, 12.0F, 0.5F, 0.0F, false);
		bipedBodyWear.setTextureOffset(7, 37).addBox(-4.0F, 0.0F, -2.5F, 2.0F, 12.0F, 0.5F, 0.0F, false);
		bipedBodyWear.setTextureOffset(64, 0).addBox(4.001F, 0.0F, -2.0F, 0.0F, 12.0F, 4.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(72, 0).addBox(-4.001F, 0.0F, -2.0F, 0.0F, 12.0F, 4.0F, 0.0F, false);

		bipedLeftArm = new MMOModelRenderer(this);
		bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
		bipedLeftArm.setTextureOffset(32, 48).addBox(-1F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedLeftArmwear = new MMOModelRenderer(this);
		bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArmwear.setTextureOffset(37, 37).addBox(-1F, -2.0F, -2.5F, 4.0F, 10.0F, 0.5F, 0.0F, false);
		bipedLeftArmwear.setTextureOffset(73, 16).addBox(3F, -2.0F, -2.0F, 0.5F, 10.0F, 4.0F, 0.0F, false);
		bipedLeftArmwear.setTextureOffset(47, 37).addBox(-1F, -2.0F, 2.0F, 4.0F, 10.0F, 0.5F, 0.0F, true);
		bipedLeftArmwear.setTextureOffset(68, 50).addBox(-1.001F, -2.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, false);
		
		bipedRightArm = new MMOModelRenderer(this);
		bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
		bipedRightArm.setTextureOffset(40, 16).addBox(-3F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightArmwear = new MMOModelRenderer(this);
		bipedRightArmwear.setRotationPoint(-5F, 2F, 0.0F);
		bipedRightArmwear.setTextureOffset(17, 37).addBox(-3F, -2.0F, -2.5F, 4.0F, 10.0F, 0.5F, 0.0F, false);
		bipedRightArmwear.setTextureOffset(64, 16).addBox(-3.5F, -2.0F, -2.0F, 0.5F, 10.0F, 4.0F, 0.0F, false);
		bipedRightArmwear.setTextureOffset(27, 37).addBox(-3F, -2.0F, 2.0F, 4.0F, 10.0F, 0.5F, 0.0F, true);
		bipedRightArmwear.setTextureOffset(76, 50).addBox(1.001F, -2.0F, -2.0F, 0.0F, 10.0F, 4.0F, 0.0F, false);
		
		bipedLeftLeg = new MMOModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		
		bipedRightLeg = new MMOModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		toothLeft = new MMOModelRenderer(this);
		toothLeft.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(toothLeft);
		toothLeft.setTextureOffset(36, 4).addBox(1.0F, -6.0F, -3.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		toothLeft.setTextureOffset(36, 4).addBox(1.5F, -5.0F, -3.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

		toothRight = new MMOModelRenderer(this);
		toothRight.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(toothRight);
		toothRight.setTextureOffset(40, 4).addBox(-2.0F, -6.0F, -3.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		toothRight.setTextureOffset(40, 4).addBox(-2.0F, -5.0F, -3.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

		earLeft = new MMOModelRenderer(this);
		earLeft.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(earLeft);
		earLeft.setTextureOffset(57, 10).addBox(4.0F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		earLeft.setTextureOffset(52, 10).addBox(4.5F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		earLeft.setTextureOffset(52, 10).addBox(4.0F, -8.0F, 0.5F, 0.5F, 0.5F, 1.0F, 0.0F, false);

		earRight = new MMOModelRenderer(this);
		earRight.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(earRight);
		earRight.setTextureOffset(52, 10).addBox(-4.5F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		earRight.setTextureOffset(52, 10).addBox(-5.0F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		earRight.setTextureOffset(52, 10).addBox(-4.5F, -8.0F, 0.5F, 0.5F, 0.5F, 1.0F, 0.0F, false);
	}
	
	@Override
	protected Iterable<ModelRenderer> getHeadParts() {
		return ImmutableList.of(bipedHead);
	}
	
	@Override
	protected Iterable<ModelRenderer> getBodyParts() {
		return ImmutableList.of(bipedBody, bipedBodyWear, bipedHeadwear, bipedRightLeg, bipedLeftLeg, bipedLeftArm, bipedRightArm, bipedLeftArmwear, bipedRightArmwear);
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/vampire/vampire.png");
	}

}
