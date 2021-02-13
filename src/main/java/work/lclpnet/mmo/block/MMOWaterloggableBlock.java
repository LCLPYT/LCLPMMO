/*
 * Scheme inspired by MrCrayfish's Furniture mod.
 * https://github.com/MrCrayfish/MrCrayfishFurnitureMod
 */

package work.lclpnet.mmo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nonnull;

public class MMOWaterloggableBlock extends MMOBlock implements IWaterLoggable{

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
    public MMOWaterloggableBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, Boolean.valueOf(false)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
    	FluidState fluidState = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @SuppressWarnings("deprecation")
	@Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }
    
	@Nonnull
	@Override
	@SuppressWarnings("deprecation")
	public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if(stateIn.get(WATERLOGGED))
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));

		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
}
