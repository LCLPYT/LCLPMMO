package work.lclpnet.mmo.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.entity.PixieEntity;

public class PixieModel extends SegmentedModel<PixieEntity> {

	public final ModelRenderer body;
	public final ModelRenderer leftTopWing;
	public final ModelRenderer rightTopWing;
	public final ModelRenderer rightBottomWing;
	public final ModelRenderer leftBottomWing;

	public PixieModel() {
		textureWidth = 16;
		textureHeight = 16;

		final float pi = (float) Math.PI, y0 = 22.5F;

		body = new ModelRenderer(this, 0, 0);
		body.setRotationPoint(-1F, y0, -1F);
		body.addBox(0F, -2F, 0F, 2, 2, 2);
		setRotation(body, 0F, 0F, 0F);

		leftTopWing = new ModelRenderer(this, 10, 0);
		leftTopWing.setRotationPoint(-1F, y0 - 2F, 1F);
		leftTopWing.addBox(0F, 0F, 0F, 0F, 1F, 3F, 0.001F);
		setRotation(leftTopWing, pi * 0.19F, pi * 1.59F, pi * 0F);

		rightTopWing = new ModelRenderer(this, 10, 4);
		rightTopWing.setRotationPoint(1F, y0 - 2F, 1F);
		rightTopWing.addBox(0F, 0F, 0F, 0F, 1F, 3F, 0.001F);
		setRotation(rightTopWing, pi * 0.19F, pi * 0.41F, pi * 0F);

		rightBottomWing = new ModelRenderer(this, 10, 8);
		rightBottomWing.setRotationPoint(1F, y0, 1F);
		rightBottomWing.addBox(0F, 0F, 0F, 0F, 1F, 2F, 0.001F);
		setRotation(rightBottomWing, pi * -0.09F, pi * 0.41F, pi * 0F);

		leftBottomWing = new ModelRenderer(this, 10, 11);
		leftBottomWing.setRotationPoint(-1F, y0, 1F);
		leftBottomWing.addBox(0F, 0F, 0F, 0F, 1F, 2F, 0.001F);
		setRotation(leftBottomWing, pi * -0.09F, pi * 1.59F, pi * 0F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return ImmutableList.of(body, leftTopWing, rightTopWing, rightBottomWing, leftBottomWing);
	}

	@Override
	public void setRotationAngles(PixieEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {
		boolean onGroundMotionless = entityIn.isOnGround() && entityIn.getMotion().lengthSquared() < 1.0E-2D;
		if (!onGroundMotionless) {
			float pi = (float) Math.PI;
			float rot = MathHelper.cos(ageInTicks) * pi;
			
			this.leftTopWing.rotateAngleY = pi * 1.49F + rot * 0.25F;
			this.leftTopWing.rotateAngleX = pi * 0.19F + rot * 0.05F;
			
			this.rightTopWing.rotateAngleY = pi * 0.51F - rot * 0.25F;
			this.rightTopWing.rotateAngleX = pi * 0.19F + rot * 0.05F;
			
			this.rightBottomWing.rotateAngleY = pi * 0.51F - rot * 0.25F;
			this.rightBottomWing.rotateAngleX = pi * -0.09F - rot * 0.025F;
			
			this.leftBottomWing.rotateAngleY = pi * 1.49F + rot * 0.25F;
			this.leftBottomWing.rotateAngleX = pi * -0.09F - rot * 0.025F;
		}
	}

}
