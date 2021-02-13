package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.mmo.render.ClientRenderHandler;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(EntityRendererManager.class)
@OnlyIn(Dist.CLIENT)
public abstract class MixinEntityRendererManager {

	@Inject(
			method = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;renderBoundingBox("
					+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
					+ "Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
					+ "Lnet/minecraft/entity/Entity;"
					+ "FFF"
					+ ")V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/renderer/WorldRenderer;drawBoundingBox("
									+ "Lcom/mojang/blaze3d/matrix/MatrixStack;"
									+ "Lcom/mojang/blaze3d/vertex/IVertexBuilder;"
									+ "Lnet/minecraft/util/math/AxisAlignedBB;"
									+ "FFFF"
									+ ")V",
									shift = Shift.BEFORE
							),
					cancellable = true,
					locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onDrawBoundingBox(MatrixStack matrixStackIn, IVertexBuilder bufferIn, Entity en, float red, float green, float blue, CallbackInfo ci,
			AxisAlignedBB axisalignedbb) {
		float scaleWidth = MMOMonsterAttributes.getScaleWidth(en), inverseWidth = 1F / scaleWidth,
				scaleHeight = MMOMonsterAttributes.getScaleHeight(en), inverseHeight = 1F / scaleHeight;
		
		matrixStackIn.scale(inverseWidth, inverseHeight, inverseWidth);
		WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn, axisalignedbb, red, green, blue, 1.0F);
		ci.cancel();
	}
	
	@Inject(
			method = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;getRenderer("
					+ "Lnet/minecraft/entity/Entity;"
					+ ")Lnet/minecraft/client/renderer/entity/EntityRenderer;",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;getSkinType()Ljava/lang/String;"
							),
					cancellable = true
			)
	public void onGetSkinType(Entity en, CallbackInfoReturnable<EntityRenderer<?>> cir) {
		AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) en;
		PlayerRenderer renderer = ClientRenderHandler.getPlayerRenderer(player);
		if(renderer == null) return;
		
		cir.setReturnValue(renderer);
		cir.cancel();
	}

}
