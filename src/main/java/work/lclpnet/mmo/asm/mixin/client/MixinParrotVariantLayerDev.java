package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.facade.race.MMORace;

@Mixin(ParrotVariantLayer.class)
public class MixinParrotVariantLayerDev {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "lambda$renderParrot$1(Lcom/mojang/blaze3d/matrix/MatrixStack;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/nbt/CompoundNBT;IFFFFLnet/minecraft/entity/EntityType;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/matrix/MatrixStack;translate(DDD)V",
                    remap = true
            ),
            remap = false
    )
    private void onParrotRender(MatrixStack matrixStackIn, boolean leftShoulderIn, PlayerEntity playerEntityIn, IRenderTypeBuffer renderTypeBufferIn, CompoundNBT nbt, int packedLight, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch, EntityType<?> entityType, CallbackInfo ci) {
        MMORace race = MMORace.getRaceFromPlayer(playerEntityIn);
        if(race != null) race.doParrotTranslation(matrixStackIn);
    }

}
