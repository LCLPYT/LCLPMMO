package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.facade.race.MMORace;

@Mixin(HeadLayer.class)
public class MixinHeadLayer {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/model/ModelRenderer;translateRotate(Lcom/mojang/blaze3d/matrix/MatrixStack;)V"
            )
    )
    private void onRenderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        MMORace race = MMORace.getRaceFromPlayer(entitylivingbaseIn);
        if (race != null) race.doHeadItemTranslation(matrixStackIn);
    }

}
