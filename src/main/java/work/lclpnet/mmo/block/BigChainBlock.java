package work.lclpnet.mmo.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.mmocontent.block.ext.MMOPillarBlock;

public class BigChainBlock extends MMOPillarBlock implements Waterloggable {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape Y_SHAPE = Block.createCuboidShape(1D, 0D, 1D, 15D, 16D, 15D);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(1D, 1D, 0D, 15D, 15D, 16D);
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0D, 1D, 1D, 16D, 15D, 15D);

    public BigChainBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.GRAY)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, 2)
                .nonOpaque()
                .strength(5.0F, 6.0F)
                .sounds(BlockSoundGroup.METAL));

        setDefaultState(getDefaultState()
                .with(WATERLOGGED, false)
                .with(FACING, Direction.NORTH)
                .with(AXIS, Direction.Axis.Y));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(AXIS)) {
            case X:
            default:
                return X_SHAPE;
            case Z:
                return Z_SHAPE;
            case Y:
                return Y_SHAPE;
        }
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return super.rotate(state, rotation)
                .with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return super.mirror(state, mirror)
                .rotate(mirror.getRotation(state.get(FACING)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState placementState = super.getPlacementState(ctx);
        if (placementState == null) return null;

        final BlockPos pos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(pos);

        final BlockPos againstPos = pos.offset(ctx.getSide().getOpposite());
        BlockState against = ctx.getWorld().getBlockState(againstPos);
        if (against.getBlock() instanceof BigChainBlock) {
            placementState = placementState.with(FACING, against.get(FACING).rotateYClockwise());
        }
        return placementState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED, FACING);
    }
}
