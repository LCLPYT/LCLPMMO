package work.lclpnet.mmo.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmofurniture.blockentity.BlockEntityUtil;
import work.lclpnet.mmofurniture.blockentity.IUpdatePacketReceiver;

public class GlassBottleBlockEntity extends BlockEntity implements IUpdatePacketReceiver {

    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public GlassBottleBlockEntity(BlockPos blockPos, BlockState state) {
        super(DecorationsModule.glassBottleBlockEntity, blockPos, state);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.readData(tag);
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        this.writeData(tag);
        super.writeNbt(tag);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        return writeData(tag);
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void onDataPacket(ClientConnection connection, BlockEntityUpdateS2CPacket packet) {
        NbtCompound tag = packet.getNbt();
        this.readData(tag);
    }

    private void readData(NbtCompound compound) {
        this.items = DefaultedList.ofSize(1, ItemStack.EMPTY);
        Inventories.readNbt(compound, items);
    }

    private NbtCompound writeData(NbtCompound compound) {
//        ItemStackSerializerHelper.saveAllItemsIncludeEmpty(compound, items, true);
        Inventories.writeNbt(compound, items);
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
