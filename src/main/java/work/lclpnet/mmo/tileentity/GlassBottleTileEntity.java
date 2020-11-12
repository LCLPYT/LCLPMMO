package work.lclpnet.mmo.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.block.MMOBlocks;
import work.lclpnet.mmo.block.MMOWaterloggableBlock;
import work.lclpnet.mmo.util.ItemStackUtils;

public class GlassBottleTileEntity extends TileEntity{

	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

	public GlassBottleTileEntity() {
		super(MMOTileEntites.GLASS_BOTTLE);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		ItemStackHelper.saveAllItems(compound, items);
		return compound;
	}

	@Override
	public void read(BlockState state, CompoundNBT compound) {
		super.read(state, compound);
		ItemStackHelper.loadAllItems(compound, items);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		ItemStackUtils.saveAllItemsIncludeEmpty(tag, items);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.handleUpdateTag(state, tag);
		ItemStackHelper.loadAllItems(tag, items);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		ItemStackUtils.saveAllItemsIncludeEmpty(nbt, items);
		return new SUpdateTileEntityPacket(getPos(), -1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		ItemStackHelper.loadAllItems(nbt, items);
	}

	public ItemStack getItem() {
		return items.get(0);
	}

	public void setItem(ItemStack item) {
		items.set(0, item);
		this.markDirty();
		updateState();
		if(!world.isRemote) world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 2);
	}

	private void updateState() {
		BlockState existing = this.world.getBlockState(getPos());
		if(work.lclpnet.mmo.util.ItemStackUtils.isAir(getItem())
				&& existing.get(GlassBottleBlock.ENABLED)) setEnabled(false);
		else if(!existing.get(GlassBottleBlock.ENABLED)) setEnabled(true);
	}
	
	private void setEnabled(boolean enabled) {
		BlockState state = MMOBlocks.GLASS_BOTTLE.getDefaultState()
				.with(GlassBottleBlock.ENABLED, enabled)
				.with(MMOWaterloggableBlock.WATERLOGGED, this.world.getFluidState(this.getPos()).getFluid() == Fluids.WATER);

		this.world.setBlockState(getPos(), state);
	}

}
