package work.lclpnet.mmo.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.util.ResourceLocation;
import work.lclpnet.mmo.client.render.model.AbstractMMOPlayerModel;
import work.lclpnet.mmo.util.MMOUtils;

import javax.annotation.Nullable;
import java.util.Objects;

public class MMOPlayerRenderer extends PlayerRenderer {

    @Nullable
    public final ResourceLocation textureLocation;

    public MMOPlayerRenderer(EntityRendererManager renderManager, AbstractMMOPlayerModel model) {
        super(renderManager, false);
        this.entityModel = Objects.requireNonNull(model);
        this.textureLocation = model.getTextureLocation();
        overrideLayers(model);
    }

    private void overrideLayers(AbstractMMOPlayerModel model) {
        BipedModel<AbstractClientPlayerEntity> armorBody = model.getArmorBody(), armorLeggings = model.getArmorLeggings();
        if (armorBody != null || armorLeggings != null) {
            MMOUtils.applyFilteredAction(this.layerRenderers, BipedArmorLayer.class::isInstance, this.layerRenderers::removeAll);

            if (armorBody == null) armorBody = new BipedModel<>(1.0F);
            else if (armorLeggings == null) armorLeggings = new BipedModel<>(0.5F);

            this.addLayer(new BipedArmorLayer<>(this, armorLeggings, armorBody));
        }
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
