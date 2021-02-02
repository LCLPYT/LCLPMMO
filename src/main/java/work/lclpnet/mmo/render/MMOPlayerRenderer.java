package work.lclpnet.mmo.render;

import java.util.Objects;

import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.render.model.AbstractMMOPlayerModel;

public class MMOPlayerRenderer extends PlayerRenderer {

	@Nullable
	public final ResourceLocation textureLocation;
	
	public MMOPlayerRenderer(EntityRendererManager renderManager, AbstractMMOPlayerModel model) {
		super(renderManager, false);
		this.entityModel = Objects.requireNonNull(model);
		this.textureLocation = model.getTextureLocation();
	}
	
	@Override
	public ResourceLocation getEntityTexture(AbstractClientPlayerEntity entity) {
		return this.textureLocation == null ? entity.getLocationSkin() : this.textureLocation;
	}
	
	@Override
	public void renderRightArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
			AbstractClientPlayerEntity playerIn) {
		
		super.renderRightArm(matrixStackIn, bufferIn, combinedLightIn, playerIn);
	}
	
	@Override
	public void renderLeftArm(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
			AbstractClientPlayerEntity playerIn) {
		super.renderLeftArm(matrixStackIn, bufferIn, combinedLightIn, playerIn);
	}
	
}
