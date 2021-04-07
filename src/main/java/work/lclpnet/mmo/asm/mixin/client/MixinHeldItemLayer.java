package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.facade.race.MMORace;

@Mixin(HeldItemLayer.class)
public class MixinHeldItemLayer {

    @Inject(
            method = "func_229135_a_(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Lnet/minecraft/util/HandSide;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("HEAD")
    )
    public void onRenderItemPre(LivingEntity livingEntity, ItemStack itemStack, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack matrixStack, IRenderTypeBuffer p_229135_6_, int p_229135_7_, CallbackInfo ci) {
        MMORace race = MMORace.getRaceFromPlayer(livingEntity);
        if (conditionRenderItem(livingEntity, itemStack, race)) {
            matrixStack.push();
            race.doTridentTranslation(matrixStack);
        }
    }

    @Inject(
            method = "func_229135_a_(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;Lnet/minecraft/util/HandSide;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("TAIL")
    )
    public void onRenderItemPost(LivingEntity livingEntity, ItemStack itemStack, ItemCameraTransforms.TransformType p_229135_3_, HandSide p_229135_4_, MatrixStack matrixStack, IRenderTypeBuffer p_229135_6_, int p_229135_7_, CallbackInfo ci) {
        if (conditionRenderItem(livingEntity, itemStack, MMORace.getRaceFromPlayer(livingEntity))) matrixStack.pop();
    }

    private boolean conditionRenderItem(LivingEntity livingEntity, ItemStack itemStack, MMORace race) {
        return !itemStack.isEmpty() && race != null && Items.TRIDENT.equals(itemStack.getItem()) && livingEntity.isHandActive();
    }

}
