package work.lclpnet.mmo.client.module;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtil;
import work.lclpnet.mmo.blockentity.GlassBottleBlockEntity;
import work.lclpnet.mmo.client.render.blockentity.GlassBottleBlockEntityRenderer;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmocontent.client.render.block.MMORenderLayers;

public class DecorationsClientModule implements IClientModule {

    @Override
    public void register() {
        MMORenderLayers.setBlockRenderType(DecorationsModule.glassBottleBlock, RenderLayer.getTranslucent());

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) return -1;

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof GlassBottleBlockEntity) {
                ItemStack item = ((GlassBottleBlockEntity) blockEntity).getItem();
                return PotionUtil.getColor(item);
            }

            return BiomeColors.getWaterColor(world, pos);
        }, DecorationsModule.glassBottleBlock);

        // register GlassBottleBlockEntityRenderer, for sodium compat
        BlockEntityRendererRegistry.register(DecorationsModule.glassBottleBlockEntity, ctx -> new GlassBottleBlockEntityRenderer());
    }
}
