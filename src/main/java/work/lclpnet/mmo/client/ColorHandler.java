package work.lclpnet.mmo.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.block.MMOBlocks;
import work.lclpnet.mmo.tileentity.GlassBottleTileEntity;

@OnlyIn(Dist.CLIENT)
public class ColorHandler {

    public static void init() {
        Minecraft.getInstance().getBlockColors().register((state, light, pos, tintIndex) -> {
            if (light == null || pos == null) return -1;

            TileEntity te = Minecraft.getInstance().world.getTileEntity(pos);
            if (te instanceof GlassBottleTileEntity) {
                ItemStack item = ((GlassBottleTileEntity) te).getItem();
                return PotionUtils.getColor(item);
            }

            return BiomeColors.getWaterColor(light, pos);
        }, MMOBlocks.GLASS_BOTTLE);
    }
}
