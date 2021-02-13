/*
 * Scheme inspired by MrCrayfish's Furniture mod.
 * https://github.com/MrCrayfish/MrCrayfishFurnitureMod
 */

package work.lclpnet.mmo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public class MMOHorizontalBlock extends MMOBlock{

	public static final DirectionProperty DIRECTION = BlockStateProperties.HORIZONTAL_FACING;

	public MMOHorizontalBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(DIRECTION, context.getPlacementHorizontalFacing());
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.toRotation(state.get(DIRECTION)));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(DIRECTION);
	}	

}
