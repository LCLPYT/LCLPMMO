package work.lclpnet.mmo.client.render.model;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import work.lclpnet.mmo.client.render.MMOModelRenderer;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class AbstractMMOPlayerModel extends PlayerModel<AbstractClientPlayerEntity> {

	public AbstractMMOPlayerModel(float modelSize, boolean smallArmsIn) {
		super(modelSize, smallArmsIn);
	}

	public abstract ResourceLocation getTextureLocation();

	protected abstract void populate();

	/**
	 * Method to override a BipedModel's parts.
	 * Used for both the model and the armor (model) for specific models.
	 *
	 * Element properties passed to the method are similar to uniform variables from GLSL.
	 *
	 * @param model The model to override.
	 * @param ctx The context of the model creation.
	 * @param headProps Element properties for the head.
	 * @param bodyProps Element properties for the body.
	 * @param leftArmProps Element properties for the left arm.
	 * @param rightArmProps Element properties for the right arm.
	 * @param leftLegProps Element properties for the left leg.
	 * @param rightLegProps Element properties for the right leg.
	 */
	protected void overrideBipedModel(BipedModel<AbstractClientPlayerEntity> model, ModelContext ctx, BoxProperties headProps, BoxProperties bodyProps,
								   BoxProperties leftArmProps, BoxProperties rightArmProps, BoxProperties leftLegProps, BoxProperties rightLegProps) {
		loseModelReferences(model);

		ModelRenderer head = makeModelRenderer(head(headProps, ctx), model);
		if(head != null) model.bipedHead = head;

		ModelRenderer body = makeModelRenderer(body(bodyProps, ctx), model);
		if(body != null) model.bipedBody = body;

		ModelRenderer leftArm = makeModelRenderer(leftArm(leftArmProps, ctx), model);
		if(leftArm != null) model.bipedLeftArm = leftArm;

		ModelRenderer rightArm = makeModelRenderer(rightArm(rightArmProps, ctx), model);
		if(rightArm != null) model.bipedRightArm = rightArm;

		ModelRenderer leftLeg = makeModelRenderer(leftLeg(leftLegProps, ctx), model);
		if(leftLeg != null) model.bipedLeftLeg = leftLeg;

		ModelRenderer rightLeg = makeModelRenderer(rightLeg(rightLegProps, ctx), model);
		if(rightLeg != null) model.bipedRightLeg = rightLeg;
	}

	private ModelRenderer getScaledMR(MMOModelRenderer.Properties defaultVal, MMOModelRenderer.Properties relativeVal, Model owner) {
		return defaultVal.difference(relativeVal).make(owner);
	}

	protected void overrideModelScale(final BipedModel<AbstractClientPlayerEntity> model, final float delta, ModelContext ctx) {
		loseModelReferences(model);

		final Vector3f ZERO = new Vector3f(0F, 0F, 0F);
		final BoxProperties NONE = new BoxProperties(0, 0, 0F, false);

		MMOModelRenderer.Properties head = head(NONE, ctx),
				body = body(NONE, ctx),
				leftArm = leftArm(NONE, ctx),
				rightArm = rightArm(NONE, ctx),
				leftLeg = leftLeg(NONE, ctx),
				rightLeg = rightLeg(NONE, ctx);

		if(head != null) {
			model.bipedHead = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					ZERO,
					new Vector3f(-4.0F, -8.0F, -4.0F),
					new Vector3f(8.0F, 8.0F, 8.0F),
					0, 0, delta, false
			), head, model);
		}

		if(body != null) {
			model.bipedBody = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					ZERO,
					new Vector3f(-4.0F, 0.0F, -2.0F),
					new Vector3f(8.0F, 12.0F, 4.0F),
					16, 16, delta, false
			), body, model);
		}

		if(leftArm != null) {
			model.bipedLeftArm = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					new Vector3f(5F, 2F, 0F),
					new Vector3f(-1.0F, -2.0F, -2.0F),
					new Vector3f(4.0F, 12.0F, 4.0F),
					40, 16, delta, true
			), leftArm, model);
		}

		if(rightArm != null) {
			model.bipedRightArm = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					new Vector3f(-5F, 2F, 0F),
					new Vector3f(-3.0F, -2.0F, -2.0F),
					new Vector3f(4.0F, 12.0F, 4.0F),
					40, 16, delta, false
			), rightArm, model);
		}

		if(leftLeg != null) {
			model.bipedLeftLeg = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					new Vector3f(1.9F, 12F, 0F),
					new Vector3f(-2.0F, 0.0F, -2.0F),
					new Vector3f(4.0F, 12.0F, 4.0F),
					0, 16, delta, true
			), leftLeg, model);
		}

		if(rightLeg != null) {
			model.bipedRightLeg = getScaledMR(new MMOModelRenderer.Properties(
					ZERO,
					new Vector3f(-1.9F, 12F, 0F),
					new Vector3f(-2.0F, 0.0F, -2.0F),
					new Vector3f(4.0F, 12.0F, 4.0F),
					0, 16, delta, false
			), rightLeg, model);
		}
	}

	protected void loseModelReferences() {
		loseModelReferences(this);
	}

	public static void loseModelReferences(BipedModel<AbstractClientPlayerEntity> model) {
		if(model instanceof PlayerModel) ((PlayerModel<AbstractClientPlayerEntity>) model).modelRenderers.clear();
	}

	@Nullable
	protected MMOModelRenderer.Properties head(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected MMOModelRenderer.Properties body(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected MMOModelRenderer.Properties leftArm(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected MMOModelRenderer.Properties rightArm(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected MMOModelRenderer.Properties leftLeg(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected MMOModelRenderer.Properties rightLeg(BoxProperties props, ModelContext ctx) {
		return null;
	}

	@Nullable
	protected final MMOModelRenderer makeModelRenderer(MMOModelRenderer.Properties properties, Model owner) {
		if(properties == null || owner == null) return null;
		return properties.make(owner);
	}

	public BipedModel<AbstractClientPlayerEntity> getArmorBody() {
		return null;
	}

	public BipedModel<AbstractClientPlayerEntity> getArmorLeggings() {
		return null;
	}

	@Override
	public ModelRenderer getRandomModelRenderer(Random randomIn) {
		try {
			return super.getRandomModelRenderer(randomIn);
		} catch (IllegalArgumentException ex) {
			return null;
		}
	}

	protected final BipedModel<AbstractClientPlayerEntity> getDefaultBodyArmor() {
		return getScaledArmorModel(1.0F);
	}
	protected final BipedModel<AbstractClientPlayerEntity> getDefaultLeggingsArmor() {
		return getScaledArmorModel(0.5F);
	}

	private BipedModel<AbstractClientPlayerEntity> getScaledArmorModel(float delta) {
		BipedModel<AbstractClientPlayerEntity> bodyModel = new BipedModel<>(delta);

		this.overrideModelScale(bodyModel, delta, ModelContext.ARMOR);

		return bodyModel;
	}

	public static class BoxProperties {

		public int u, v;
		public float delta;
		public boolean mirror;

		public BoxProperties(int u, int v, float delta, boolean mirror) {
			this.u = u;
			this.v = v;
			this.delta = delta;
			this.mirror = mirror;
		}

	}

	public enum ModelContext {

		DEFAULT,
		ARMOR

	}

}
