package work.lclpnet.mmo.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmofurniture.blockentity.BlockEntityUtil;
import work.lclpnet.mmofurniture.blockentity.IUpdatePacketReceiver;

public class GlassBottleBlockEntity extends BlockEntity implements IUpdatePacketReceiver {

    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public GlassBottleBlockEntity() {
        super(DecorationsModule.glassBottleBlockEntity);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.readData(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.writeData(tag);
        return super.toTag(tag);
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        CompoundTag tag = super.toInitialChunkDataTag();
        return writeData(tag);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(getPos(), 0, toInitialChunkDataTag());
    }

    @Override
    public void onDataPacket(ClientConnection connection, BlockEntityUpdateS2CPacket packet) {
        CompoundTag tag = packet.getCompoundTag();
        this.readData(tag);
    }

    private void readData(CompoundTag compound) {
        this.items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.fromTag(compound, items);
    }

    private CompoundTag writeData(CompoundTag compound) {
//        ItemStackSerializerHelper.saveAllItemsIncludeEmpty(compound, items, true);
        Inventories.toTag(compound, items);
        return compound;
    }

    public ItemStack getItem() {
        return items.get(0);
    }

    public void setItem(ItemStack item) {
        items.set(0, item);
        if (this.world != null && !this.world.isClient) {
            BlockEntityUtil.sendUpdatePacket(this);
            this.world.setBlockState(getPos(), this.getCachedState().with(GlassBottleBlock.ENABLED, !item.isEmpty()));
        }
    }
}
