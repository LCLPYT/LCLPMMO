package work.lclpnet.mmo.render.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.entity.PixieEntity;

public class OldPixieModel extends SegmentedModel<PixieEntity> {

	public ModelRenderer horn_east;
	public ModelRenderer horn_west;
	public ModelRenderer horn_south;
	public ModelRenderer horn_north;
	public ModelRenderer horn_lower;
	public ModelRenderer horn_upper;
	public ModelRenderer e7;
	public ModelRenderer e8;
	public ModelRenderer e9;
	public ModelRenderer e10;
	public ModelRenderer e11;
	public ModelRenderer e12;
	public ModelRenderer e13;
	public ModelRenderer e14;
	public ModelRenderer e15;
	public ModelRenderer e16;
	public ModelRenderer e17;
	public ModelRenderer e18;
	public ModelRenderer e19;
	public ModelRenderer e20;
	public ModelRenderer e21;
	public ModelRenderer e22;
	public ModelRenderer e23;
	public ModelRenderer e24;
	public ModelRenderer e25;
	public ModelRenderer e26;
	public ModelRenderer e27;
	public ModelRenderer e28;
	public ModelRenderer e29;
	public ModelRenderer e30;
	public ModelRenderer e31;
	private final ImmutableList<ModelRenderer> parts;

	public OldPixieModel() {
		textureWidth = 64;
		textureHeight = 64;

		float y0 = 16F; // 31.5F
		
		horn_east = new ModelRenderer(this, 24, 12);
		horn_east.setRotationPoint(-7F, y0, -0.5F);
		horn_east.addBox(0F, 0F, 0F, 3, 1, 1);
		horn_east.setTextureSize(64, 64);
		horn_east.mirror = false;
		setRotation(horn_east, 0F, 0F, 0F);
		horn_west = new ModelRenderer(this, 24, 10);
		horn_west.setRotationPoint(4F, y0, -0.5F);
		horn_west.addBox(0F, 0F, 0F, 3, 1, 1);
		horn_west.setTextureSize(64, 64);
		horn_west.mirror = false;
		setRotation(horn_west, 0F, 0F, 0F);
		horn_south = new ModelRenderer(this, 52, 28);
		horn_south.setRotationPoint(-0.5F, y0, 4F);
		horn_south.addBox(0F, 0F, 0F, 1, 1, 3);
		horn_south.setTextureSize(64, 64);
		horn_south.mirror = false;
		setRotation(horn_south, 0F, 0F, 0F);
		horn_north = new ModelRenderer(this, 24, 23);
		horn_north.setRotationPoint(-0.5F, y0, -7F);
		horn_north.addBox(0F, 0F, 0F, 1, 1, 3);
		horn_north.setTextureSize(64, 64);
		horn_north.mirror = false;
		setRotation(horn_north, 0F, 0F, 0F);
		horn_lower = new ModelRenderer(this, 20, 18);
		horn_lower.setRotationPoint(-0.5F, 25F, -0.5F);
		horn_lower.addBox(0F, -3F, 0F, 1, 3, 1);
		horn_lower.setTextureSize(64, 64);
		horn_lower.mirror = false;
		setRotation(horn_lower, 0F, 0F, 0F);
		horn_upper = new ModelRenderer(this, 20, 13);
		horn_upper.setRotationPoint(-0.5F, 36F, -0.5F);
		horn_upper.addBox(0F, -3F, 0F, 1, 3, 1);
		horn_upper.setTextureSize(64, 64);
		horn_upper.mirror = false;
		setRotation(horn_upper, 0F, 0F, 0F);
		e7 = new ModelRenderer(this, 0, 22);
		e7.setRotationPoint(-4F, 29F, -2F);
		e7.addBox(0F, -6F, 0F, 8, 6, 4);
		e7.setTextureSize(64, 64);
		e7.mirror = false;
		setRotation(e7, 0F, 0F, 0F);
		e8 = new ModelRenderer(this, 0, 7);
		e8.setRotationPoint(-4F, 30F, -3F);
		e8.addBox(0F, -4F, 0F, 8, 4, 1);
		e8.setTextureSize(64, 64);
		e8.mirror = false;
		setRotation(e8, 0F, 0F, 0F);
		e9 = new ModelRenderer(this, 0, 2);
		e9.setRotationPoint(-4F, 30F, 2F);
		e9.addBox(0F, -4F, 0F, 8, 4, 1);
		e9.setTextureSize(64, 64);
		e9.mirror = false;
		setRotation(e9, 0F, 0F, 0F);
		e10 = new ModelRenderer(this, 0, 17);
		e10.setRotationPoint(-3F, 28F, -2F);
		e10.addBox(0F, -1F, 0F, 6, 1, 4);
		e10.setTextureSize(64, 64);
		e10.mirror = false;
		setRotation(e10, 0F, 0F, 0F);
		e11 = new ModelRenderer(this, 0, 0);
		e11.setRotationPoint(-3F, 29F, -3F);
		e11.addBox(0F, -1F, 0F, 6, 1, 1);
		e11.setTextureSize(64, 64);
		e11.mirror = false;
		setRotation(e11, 0F, 0F, 0F);
		e12 = new ModelRenderer(this, 32, 25);
		e12.setRotationPoint(-3F, 29F, 2F);
		e12.addBox(0F, -1F, 0F, 6, 1, 1);
		e12.setTextureSize(64, 64);
		e12.mirror = false;
		setRotation(e12, 0F, 0F, 0F);
		e13 = new ModelRenderer(this, 24, 27);
		e13.setRotationPoint(-3F, 30F, -4F);
		e13.addBox(0F, -4F, 0F, 6, 4, 1);
		e13.setTextureSize(64, 64);
		e13.mirror = false;
		setRotation(e13, 0F, 0F, 0F);
		e14 = new ModelRenderer(this, 38, 27);
		e14.setRotationPoint(-3F, 30F, 3F);
		e14.addBox(0F, -4F, 0F, 6, 4, 1);
		e14.setTextureSize(64, 64);
		e14.mirror = false;
		setRotation(e14, 0F, 0F, 0F);
		e15 = new ModelRenderer(this, 46, 25);
		e15.setRotationPoint(-3F, 34F, -3F);
		e15.addBox(0F, -1F, 0F, 6, 1, 1);
		e15.setTextureSize(64, 64);
		e15.mirror = false;
		setRotation(e15, 0F, 0F, 0F);
		e16 = new ModelRenderer(this, 32, 23);
		e16.setRotationPoint(-3F, 34F, 2F);
		e16.addBox(0F, -1F, 0F, 6, 1, 1);
		e16.setTextureSize(64, 64);
		e16.mirror = false;
		setRotation(e16, 0F, 0F, 0F);
		e17 = new ModelRenderer(this, 0, 12);
		e17.setRotationPoint(-3F, 35F, -2F);
		e17.addBox(0F, -1F, 0F, 6, 1, 4);
		e17.setTextureSize(64, 64);
		e17.mirror = false;
		setRotation(e17, 0F, 0F, 0F);
		e18 = new ModelRenderer(this, 46, 23);
		e18.setRotationPoint(-2F, 28F, -3F);
		e18.addBox(0F, -1F, 0F, 4, 1, 1);
		e18.setTextureSize(64, 64);
		e18.mirror = false;
		setRotation(e18, 0F, 0F, 0F);
		e19 = new ModelRenderer(this, 32, 21);
		e19.setRotationPoint(-2F, 28F, 2F);
		e19.addBox(0F, -1F, 0F, 4, 1, 1);
		e19.setTextureSize(64, 64);
		e19.mirror = false;
		setRotation(e19, 0F, 0F, 0F);
		e20 = new ModelRenderer(this, 32, 19);
		e20.setRotationPoint(-2F, 29F, -4F);
		e20.addBox(0F, -1F, 0F, 4, 1, 1);
		e20.setTextureSize(64, 64);
		e20.mirror = false;
		setRotation(e20, 0F, 0F, 0F);
		e21 = new ModelRenderer(this, 32, 17);
		e21.setRotationPoint(-2F, 29F, 3F);
		e21.addBox(0F, -1F, 0F, 4, 1, 1);
		e21.setTextureSize(64, 64);
		e21.mirror = false;
		setRotation(e21, 0F, 0F, 0F);
		e22 = new ModelRenderer(this, 32, 15);
		e22.setRotationPoint(-2F, 34F, -4F);
		e22.addBox(0F, -1F, 0F, 4, 1, 1);
		e22.setTextureSize(64, 64);
		e22.mirror = false;
		setRotation(e22, 0F, 0F, 0F);
		e23 = new ModelRenderer(this, 32, 13);
		e23.setRotationPoint(-2F, 34F, 3F);
		e23.addBox(0F, -1F, 0F, 4, 1, 1);
		e23.setTextureSize(64, 64);
		e23.mirror = false;
		setRotation(e23, 0F, 0F, 0F);
		e24 = new ModelRenderer(this, 32, 11);
		e24.setRotationPoint(-2F, 35F, -3F);
		e24.addBox(0F, -1F, 0F, 4, 1, 1);
		e24.setTextureSize(64, 64);
		e24.mirror = false;
		setRotation(e24, 0F, 0F, 0F);
		e25 = new ModelRenderer(this, 32, 9);
		e25.setRotationPoint(-2F, 35F, 2F);
		e25.addBox(0F, -1F, 0F, 4, 1, 1);
		e25.setTextureSize(64, 64);
		e25.mirror = false;
		setRotation(e25, 0F, 0F, 0F);
		e26 = new ModelRenderer(this, 18, 8);
		e26.setRotationPoint(4F, 31F, -1F);
		e26.addBox(0F, -2F, 0F, 1, 2, 2);
		e26.setTextureSize(64, 64);
		e26.mirror = false;
		setRotation(e26, 0F, 0F, 0F);
		e27 = new ModelRenderer(this, 18, 3);
		e27.setRotationPoint(-4.5F, 31F, -1F);
		e27.addBox(0F, -2F, 0F, 1, 2, 2);
		e27.setTextureSize(64, 64);
		e27.mirror = false;
		setRotation(e27, 0F, 0F, 0F);
		e28 = new ModelRenderer(this, 24, 20);
		e28.setRotationPoint(-1F, 27.5F, -1F);
		e28.addBox(0F, -0.5F, 0F, 2, 1, 2);
		e28.setTextureSize(64, 64);
		e28.mirror = false;
		setRotation(e28, 0F, 0F, 0F);
		e29 = new ModelRenderer(this, 24, 17);
		e29.setRotationPoint(-1F, 36F, -1F);
		e29.addBox(0F, -0.5F, 0F, 2, 1, 2);
		e29.setTextureSize(64, 64);
		e29.mirror = false;
		setRotation(e29, 0F, 0F, 0F);
		e30 = new ModelRenderer(this, 18, 0);
		e30.setRotationPoint(-1F, 31F, 4F);
		e30.addBox(0F, -2F, 0F, 2, 2, 1);
		e30.setTextureSize(64, 64);
		e30.mirror = false;
		setRotation(e30, 0F, 0F, 0F);
		e31 = new ModelRenderer(this, 24, 14);
		e31.setRotationPoint(-1F, 31F, -4.5F);
		e31.addBox(0F, -2F, 0F, 2, 2, 1);
		e31.setTextureSize(64, 64);
		e31.mirror = false;
		setRotation(e31, 0F, 0F, 0F);

//		this.parts = ImmutableList.of(horn_east, horn_west, horn_south, horn_north, horn_lower, horn_upper);
		this.parts = ImmutableList.of(horn_east, horn_west, horn_south, horn_north, horn_upper, horn_lower, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19, e20, e21, e22, e23, e24, e25, e26, e27, e28, e29, e30, e31);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public Iterable<ModelRenderer> getParts() {
		return parts;
	}

	@Override
	public void setRotationAngles(PixieEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch) {

		for(ModelRenderer r : parts) 
			r.rotateAngleX = 0.2F * MathHelper.sin(ageInTicks) + 0.4F;
	}

}
