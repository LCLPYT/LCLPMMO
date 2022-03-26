package work.lclpnet.mmo.client.render.blockentity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import work.lclpnet.mmo.blockentity.GlassBottleBlockEntity;

import java.util.Random;

public class GlassBottleBlockEntityRenderer implements BlockEntityRenderer<GlassBottleBlockEntity> {

    @Override
    public void render(GlassBottleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        /* This code uses the vanilla rendering pipeline.
         * This is needed for sodium compatibility, because the liquid would otherwise not be rendered, because Sodium doesn't do transparency sorting.
         * Remove this renderer, when sodium implemented this feature: https://github.com/CaffeineMC/sodium-fabric/issues/38
         * This might not be the perfect workaround, but it is working. Keep in mind, this is only temporary.
         * NOTE: also remove GlassBottleBlock::getRenderType
         */
        MinecraftClient client = MinecraftClient.getInstance();
        BlockRenderManager blockRenderManager = client.getBlockRenderManager();
        blockRenderManager.renderBlock(entity.getCachedState(), entity.getPos(), entity.getWorld(), matrices, vertexConsumers.getBuffer(RenderLayer.getTranslucent()), true, new Random());
    }
}
