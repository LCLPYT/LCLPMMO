package work.lclpnet.mmo.event.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class GlassBottleEvent extends PlayerBlockEvent {

    protected ItemStack item;

    protected GlassBottleEvent(IWorld world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack item) {
        super(world, pos, state, player);
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Cancelable
    public static class Fill extends GlassBottleEvent {

        public Fill(IWorld world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack item) {
            super(world, pos, state, player, item);
        }
    }

    @Cancelable
    public static class Empty extends GlassBottleEvent {

        public Empty(IWorld world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack item) {
            super(world, pos, state, player, item);
        }
    }
}
