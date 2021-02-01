package work.lclpnet.mmo.render.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.LCLPMMO;

public class VampirePlayerModel extends AbstractMMOPlayerModel {

	private ModelRenderer tooth_left;
	private ModelRenderer tooth_right;
	private ModelRenderer ear_left;
	private ModelRenderer ear_right;
	
	public VampirePlayerModel() {
		super(0F, false);

		textureWidth = 96;
		textureHeight = 64;
		
		populate();
	}

	private void populate() {
		bipedBody = new ModelRenderer(this);
		bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBody.setTextureOffset(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightLeg = new ModelRenderer(this);
		bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		bipedRightLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedLeftLeg = new ModelRenderer(this);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		bipedLeftLeg.setTextureOffset(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightArm = new ModelRenderer(this);
		bipedRightArm.setRotationPoint(-5F, 2.0F, 0.0F);
		bipedRightArm.setTextureOffset(40, 16).addBox(-3F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedRightArmwear = new ModelRenderer(this);
		bipedRightArmwear.setRotationPoint(-5F, 2F, 0.0F);
		bipedRightArmwear.setTextureOffset(17, 37).addBox(-3F, -2.0F, -2.5F, 4.0F, 10.0F, 0.5F, 0.0F, false);
		bipedRightArmwear.setTextureOffset(64, 16).addBox(-3.5F, -2.0F, -2.0F, 0.5F, 10.0F, 4.0F, 0.0F, false);
		bipedRightArmwear.setTextureOffset(27, 37).addBox(-3F, -2.0F, 2.0F, 4.0F, 10.0F, 0.5F, 0.0F, true);

		bipedLeftArm = new ModelRenderer(this);
		bipedLeftArm.setRotationPoint(5F, 2.0F, 0.0F);
		bipedLeftArm.setTextureOffset(32, 48).addBox(-1F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);

		bipedLeftArmwear = new ModelRenderer(this);
		bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
		bipedLeftArmwear.setTextureOffset(37, 37).addBox(-1F, -2.0F, -2.5F, 4.0F, 10.0F, 0.5F, 0.0F, false);
		bipedLeftArmwear.setTextureOffset(73, 16).addBox(3F, -2.0F, -2.0F, 0.5F, 10.0F, 4.0F, 0.0F, false);
		bipedLeftArmwear.setTextureOffset(47, 37).addBox(-1F, -2.0F, 2.0F, 4.0F, 10.0F, 0.5F, 0.0F, true);

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHead.setTextureOffset(0, 0).addBox(-4.0F, -8.0F, -4F, 8.0F, 8.0F, 8.0F, 0.0F, false);
		
		tooth_left = new ModelRenderer(this);
		tooth_left.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(tooth_left);
		tooth_left.setTextureOffset(44, 25).addBox(1.0F, -6.0F, -3.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		tooth_left.setTextureOffset(44, 27).addBox(1.5F, -5.0F, -3.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

		tooth_right = new ModelRenderer(this);
		tooth_right.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(tooth_right);
		tooth_right.setTextureOffset(43, 25).addBox(-2.0F, -6.0F, -3.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		tooth_right.setTextureOffset(43, 26).addBox(-2.0F, -5.0F, -3.5F, 0.5F, 0.5F, 0.5F, 0.0F, false);

		ear_left = new ModelRenderer(this);
		ear_left.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(ear_left);
		ear_left.setTextureOffset(57, 10).addBox(4.0F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		ear_left.setTextureOffset(52, 10).addBox(4.5F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		ear_left.setTextureOffset(52, 10).addBox(4.0F, -8.0F, 0.5F, 0.5F, 0.5F, 1.0F, 0.0F, false);

		ear_right = new ModelRenderer(this);
		ear_right.setRotationPoint(0.0F, 5.0F, -1.0F);
		bipedHead.addChild(ear_right);
		ear_right.setTextureOffset(52, 10).addBox(-4.5F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		ear_right.setTextureOffset(52, 10).addBox(-5.0F, -8.5F, 1.0F, 0.5F, 0.5F, 1.0F, 0.0F, false);
		ear_right.setTextureOffset(52, 10).addBox(-4.5F, -8.0F, 0.5F, 0.5F, 0.5F, 1.0F, 0.0F, false);

		bipedHeadwear = new ModelRenderer(this);
		bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedHeadwear.setTextureOffset(11, 6).addBox(-4.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(-2.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(1.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(11, 5).addBox(3.0F, -5.0F, -4.5F, 1.0F, 1.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(16, 9).addBox(-4.0F, -7.0F, -4.5F, 8.0F, 2.0F, 0.5F, 0.0F, false);
		bipedHeadwear.setTextureOffset(20, 9).addBox(-3.0F, -8.0F, -4.5F, 6.0F, 1.0F, 0.5F, 0.0F, false);

		bipedBodyWear = new ModelRenderer(this);
		bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
		bipedBodyWear.setTextureOffset(22, 23).addBox(-2.0F, 1.0F, -2.75F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(22, 26).addBox(-2.0F, 2.0F, -2.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		bipedBodyWear.setTextureOffset(22, 27).addBox(1.0F, 2.0F, -2.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);

		bipedBodyWear.setTextureOffset(1, 37).addBox(2.0F, 0.0F, -2.75F, 2.0F, 12.0F, 0.5F, 0.0F, false);
		bipedBodyWear.setTextureOffset(65, 37).addBox(-4.0F, 0.0F, 1.75F, 8.0F, 12.0F, 0.5F, 0.0F, false);
		bipedBodyWear.setTextureOffset(7, 37).addBox(-4.0F, 0.0F, -2.75F, 2.0F, 12.0F, 0.5F, 0.0F, false);
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
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public ResourceLocation getTextureLocation() {
		return new ResourceLocation(LCLPMMO.MODID, "textures/entity/vampire/vampire.png");
	}

}
