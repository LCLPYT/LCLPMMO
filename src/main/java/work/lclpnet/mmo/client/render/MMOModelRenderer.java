package work.lclpnet.mmo.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.vector.Vector3f;
import work.lclpnet.mmo.asm.type.IMMOModelRenderer;
import work.lclpnet.mmo.client.render.model.AbstractMMOPlayerModel;

import java.util.Random;

public class MMOModelRenderer extends ModelRenderer implements IMMOModelRenderer {

	protected Vector3f mmoTranslatePre = null, mmoTranslatePost = null, mmoScale = null;
	
	public MMOModelRenderer(Model model) {
		super(model);
	}

	public void setMMOPreTranslation(float x, float y, float z) {
		if(mmoTranslatePre == null) mmoTranslatePre = new Vector3f();
		mmoTranslatePre.set(x, y, z);
	}

	public void setMMOPostTranslation(float x, float y, float z) {
		if(mmoTranslatePost == null) mmoTranslatePost = new Vector3f();
		mmoTranslatePost.set(x, y, z);
	}

	public void setMMOScale(float x, float y, float z) {
		if(mmoScale == null) mmoScale = new Vector3f();
		mmoScale.set(x, y, z);
	}
	
	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		boolean translate = mmoTranslatePre != null;

		if(translate) {
			matrixStackIn.push();
			doPreTranslate(matrixStackIn);
		}

		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if(translate) matrixStackIn.pop();
	}

	@Override
	public void postTranslateRotate(MatrixStack matrixStack) {
		if (this.mmoScale != null) doScale(matrixStack);
		if (this.mmoTranslatePost != null) doPostTranslate(matrixStack);
	}

	protected void doPreTranslate(MatrixStack matrixStack) {
		matrixStack.translate(mmoTranslatePre.getX(), mmoTranslatePre.getY(), mmoTranslatePre.getZ());
	}

	protected void doPostTranslate(MatrixStack matrixStack) {
		matrixStack.translate(mmoTranslatePost.getX(), mmoTranslatePost.getY(), mmoTranslatePost.getZ());
	}

	protected void doScale(MatrixStack matrixStack) {
		/*
		I don't know where a and b come from.
		They are approximations for parameters a and b of a function modelling the offset.

		\[ offsetY(scaleY) = (a * scaleY - a) / (b * scaleY) \]

		This function was found from plotting test values.
		 */
		final float a = 0.2763F, b = -0.34F;
		float offsetY = (a * mmoScale.getY() - a) / (b * mmoScale.getY());

		matrixStack.scale(mmoScale.getX(), mmoScale.getY(), mmoScale.getZ());
		matrixStack.translate(0F, offsetY, 0F);
	}

	@Override
	public ModelBox getRandomCube(Random randomIn) {
		try {
			return super.getRandomCube(randomIn);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	public static class Properties {

		public final Vector3f preTranslation, rotationPoint, location, dimensions;
		public final int u, v;
		public final float delta;
		public final boolean mirror;
		public Vector3f scale = null, postTranslation = null;

		public Properties(Vector3f preTranslation, Vector3f rotationPoint, Vector3f location, Vector3f dimensions, AbstractMMOPlayerModel.BoxProperties boxProps) {
			this(preTranslation, rotationPoint, location, dimensions, boxProps.u, boxProps.v, boxProps.delta, boxProps.mirror);
		}

		public Properties(Vector3f preTranslation, Vector3f rotationPoint, Vector3f location, Vector3f dimensions, int u, int v, float delta, boolean mirror) {
			this.preTranslation = preTranslation;
			this.rotationPoint = rotationPoint;
			this.location = location;
			this.dimensions = dimensions;
			this.u = u;
			this.v = v;
			this.delta = delta;
			this.mirror = mirror;
		}

		public MMOModelRenderer make(Model owner) {
			MMOModelRenderer renderer = new MMOModelRenderer(owner);

			renderer.setMMOPreTranslation(this.preTranslation.getX(), this.preTranslation.getY(), this.preTranslation.getZ());
			if(this.postTranslation != null) renderer.setMMOPostTranslation(this.postTranslation.getX(), this.postTranslation.getY(), this.postTranslation.getZ());
			if(this.scale != null) renderer.setMMOScale(this.scale.getX(), this.scale.getY(), this.scale.getZ());

			renderer.setRotationPoint(this.rotationPoint.getX(), this.rotationPoint.getY(), this.rotationPoint.getZ());
			renderer.setTextureOffset(this.u, this.v)
					.addBox(this.location.getX(), location.getY(), this.location.getZ(),
							this.dimensions.getX(), this.dimensions.getY(), this.dimensions.getZ(),
							this.delta, this.mirror);

			return renderer;
		}

		public Properties difference(Properties relative) {
			Properties properties = new Properties(
					relative.preTranslation,
					this.rotationPoint,
					this.location,
					this.dimensions,
					this.u, this.v, this.delta, this.mirror
			);

			properties.scale = new Vector3f(
					relative.dimensions.getX() / this.dimensions.getX(),
					relative.dimensions.getY() / this.dimensions.getY(),
					relative.dimensions.getZ() / this.dimensions.getZ()
			);

			properties.postTranslation = relative.postTranslation;

			float scaleTranslationCorrectionY = (relative.dimensions.getY() - this.dimensions.getY()) / 16F;
			if(scaleTranslationCorrectionY != 0F) {
				if(properties.postTranslation == null) properties.postTranslation = new Vector3f();

				properties.postTranslation.add(
						0F,
						scaleTranslationCorrectionY,
						0F
				);
			}

			return properties;
		}

	}

}
