package work.lclpnet.mmo.asm.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import work.lclpnet.mmo.asm.type.IMMOModelRenderer;

@Mixin(ModelRenderer.class)
public class MixinModelRenderer implements IMMOModelRenderer {

    @Inject(
            method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/model/ModelRenderer;translateRotate(Lcom/mojang/blaze3d/matrix/MatrixStack;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void onTranslateRotateRender(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, CallbackInfo ci) {
        this.postTranslateRotate(matrixStackIn);
    }

    @Override
    public void postTranslateRotate(MatrixStack matrixStack) {
    }

}
