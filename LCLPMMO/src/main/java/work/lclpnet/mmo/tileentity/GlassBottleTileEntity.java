package work.lclpnet.mmo.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import work.lclpnet.mmo.block.GlassBottleBlock;
import work.lclpnet.mmo.block.MMOBlocks;
import work.lclpnet.mmo.block.MMOWaterloggableBlock;

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
	public void read(CompoundNBT compound) {
		super.read(compound);
		ItemStackHelper.loadAllItems(compound, items);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT tag = super.getUpdateTag();
		ItemStackHelper.saveAllItems(tag, items);
		return tag;
	}
	
	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
		ItemStackHelper.loadAllItems(tag, items);
	}
	
	public ItemStack getItem() {
		return items.get(0);
	}
	
	public void setItem(ItemStack item) {
		items.set(0, item);
		this.markDirty();
		updateState();
	}

	private void updateState() {
		BlockState existing = this.world.getBlockState(getPos());
		if(work.lclpnet.mmo.util.ItemStackHelper.isAir(getItem())
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
