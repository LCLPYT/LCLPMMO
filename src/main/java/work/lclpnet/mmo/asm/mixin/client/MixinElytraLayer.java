package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.facade.race.MMORace;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V"
            )
    )
    public void onRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        MMORace race = MMORace.getRaceFromPlayer(entity);
        if (race != null) race.doElytraTranslation(matrixStackIn);
    }

}
