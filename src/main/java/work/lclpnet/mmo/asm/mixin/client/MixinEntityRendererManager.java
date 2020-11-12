package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

}
