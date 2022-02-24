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
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmocontent.block.ext.MMOPillarBlock;
import work.lclpnet.mmofurniture.block.FurnitureHorizontalWaterloggedBlock;

public class BigChainBlock extends MMOPillarBlock implements Waterloggable, IBigChainBlock {

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

        final Direction direction = ctx.getSide();
        final BlockPos againstPos = pos.offset(direction.getOpposite());
        BlockState against = ctx.getWorld().getBlockState(againstPos);
        if (against.getBlock() instanceof BigChainBlock) {
            final Direction.Axis axis = against.get(AXIS);
            if (direction.getAxis() != axis) {
                final boolean up = getEdgeAxisUpwards(ctx.getWorld(), againstPos, axis);
                final Direction newDirection = getPlacementDirection(axis, against.get(FACING), direction, up);

                placementState = placementState.with(FACING, newDirection);
            } else {
                placementState = placementState.with(FACING, against.get(FACING).rotateYClockwise());
            }
        }
        return placementState.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    /**
     * @param oldDirection The direction property
     * @param pointDirection The direction, the new corner should point to.
     * @param up Whether the corner chain block should go up or not.
     * @return The direction for the currently placed chain block.
     */
    private Direction getPlacementDirection(Direction.Axis axis, Direction oldDirection, Direction pointDirection, boolean up) {
        final Direction.Axis chainAxis = oldDirection.getAxis();

        if (axis == Direction.Axis.Y) {
            // please don't ask
            /* Chain axis is X/Z */
            int shift = chainAxis == Direction.Axis.Z ? 1 : 0;
            if (up) shift = (shift + 1) % 2;

            int idx = pointDirection.ordinal() - 2;
            idx = Math.floorMod(idx - shift, 4);
            idx = new int[]{0, 3, 1, 2}[idx] + 2;

            return Direction.byId(idx);
        } else if (axis == Direction.Axis.X) {
            Direction newDir;
            if (pointDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                // arbitrary for some variation
                newDir = pointDirection.getAxis() == Direction.Axis.Y ? Direction.EAST : Direction.WEST;
            } else {
                newDir = pointDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.SOUTH;
            }
            if (chainAxis == Direction.Axis.Z) newDir = newDir.rotateYClockwise();
            return up ? newDir.rotateYClockwise() : newDir;
        } else {
            Direction newDir;
            if (pointDirection.getDirection() == Direction.AxisDirection.POSITIVE) {
                // arbitrary for some variation
                newDir = pointDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.SOUTH;
            } else {
                newDir = pointDirection.getAxis() == Direction.Axis.Y ? Direction.EAST : Direction.WEST;
            }
            if (chainAxis == Direction.Axis.X) newDir = newDir.rotateYClockwise();
            return up ? newDir.rotateYClockwise() : newDir;
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        final BlockState original = super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);

        if (!(newState.getBlock() instanceof IBigChainBlock)) return original;

        final Direction.Axis placementAxis = direction.getAxis();
        final Direction.Axis currentAxis = state.get(AXIS);
        if (placementAxis == currentAxis) return original;

        // determine the upwards property
        boolean up = getEdgeAxisUpwards(world, pos, currentAxis); // implemented for all axes

        // determine the dock and direction
        final Direction currentFacing = state.get(FACING);
        final Tuple properties = getProperties(currentAxis, currentFacing, direction, up);

        return DecorationsModule.bigChainCornerBlock.getDefaultState()
                .with(FurnitureHorizontalWaterloggedBlock.DIRECTION, properties.direction)
                .with(BigChainCornerBlock.UP, up)
                .with(BigChainCornerBlock.DOCK, properties.dock)
                .with(BigChainCornerBlock.AXIS, currentAxis);
    }

    private boolean getEdgeAxisUpwards(WorldAccess world, BlockPos pos, Direction.Axis currentAxis) {
        Direction dir = getAxisDirection(currentAxis, false);
        BlockPos down = pos.offset(dir);
        BlockState rel = world.getBlockState(down);
        if (rel.getBlock() instanceof IBigChainBlock) return false;

        dir = getAxisDirection(currentAxis, true);
        BlockPos up = pos.offset(dir);
        rel = world.getBlockState(up);

        if (rel.getBlock() instanceof IBigChainBlock) return true;

        return !world.isAir(up) && world.isAir(down);
    }

    private Direction getAxisDirection(Direction.Axis axis, boolean up) {
        switch (axis) {
            case Y:
                return up ? Direction.UP : Direction.DOWN;
            case X:
                return up ? Direction.EAST : Direction.WEST;
            case Z:
                return up ? Direction.SOUTH : Direction.NORTH;
            default:
                throw new AssertionError();
        }
    }

    /**
     * @param axis The axis property of the old chain.
     * @param oldDirection The facing property of the old chain, which is about to be converted to a corner.
     * @param pointDirection The direction the corner piece should point.
     * @param up Whether the new corner piece should point up or not.
     * @return A tuple with properties for the new chain.
     */
    private Tuple getProperties(final Direction.Axis axis, final Direction oldDirection, final Direction pointDirection, final boolean up) {
        /* Chain axis is X/Z */
        final Direction.Axis chainAxis = oldDirection.getAxis();
        final Direction.Axis placedAxis = pointDirection.getAxis();

        if (axis == Direction.Axis.Y) {
            Tuple dock = getPropertiesY(chainAxis, placedAxis, pointDirection);
            return up ? dock.invert() : dock;
        } else if (axis == Direction.Axis.X) {
            Direction rotated = rotateDirection(pointDirection, axis, up);
            Tuple dock = getPropertiesX(chainAxis, placedAxis, rotated);

            // don't ask why it is inconsistent to Y-Axis, it's 3 AM
            if (up && dock.direction.getAxis() == Direction.Axis.Z) {
                dock.direction = dock.direction.getOpposite();
            }

            return dock;
        } else {
            Direction rotated = rotateDirection(pointDirection, axis, up);
            Tuple dock = getPropertiesZ(chainAxis, placedAxis, rotated);

            // don't ask why it is inconsistent to Y-Axis, it's 3 AM
            if (up && dock.direction.getAxis() == Direction.Axis.X) {
                dock.direction = dock.direction.getOpposite();
            }

            return dock;
        }
    }

    private Direction rotateDirection(Direction direction, Direction.Axis axis, boolean up) {
        final Direction.Axis dirAxis = direction.getAxis();
        if (axis == dirAxis) {
            throw new IllegalArgumentException("Invalid direction for axis " + axis);
        }

        if (dirAxis != Direction.Axis.Y) return direction;

        if (axis == Direction.Axis.X) {
            if (up) return direction == Direction.UP ? Direction.EAST : Direction.WEST;
            else return direction == Direction.UP ? Direction.WEST : Direction.EAST;
        } else if (axis == Direction.Axis.Z) {
            if (up) return direction == Direction.UP ? Direction.SOUTH : Direction.NORTH;
            else return direction == Direction.UP ? Direction.NORTH : Direction.SOUTH;
        }

        return direction;
    }

    private Tuple getPropertiesY(final Direction.Axis chainAxis, final Direction.Axis placedAxis, final Direction pointDirection) {
        /* Placed axis can only be X/Z */
        if (chainAxis == Direction.Axis.X) {  // east,west
            return new Tuple(
                    placedAxis == Direction.Axis.X ? BigChainCornerBlock.ChainDock.HORIZONTAL : BigChainCornerBlock.ChainDock.VERTICAL,
                    placedAxis == Direction.Axis.X ? pointDirection : pointDirection.getOpposite()
            );
        } else if (chainAxis == Direction.Axis.Z) {  // north,south
            return new Tuple(
                    placedAxis == Direction.Axis.X ? BigChainCornerBlock.ChainDock.VERTICAL : BigChainCornerBlock.ChainDock.HORIZONTAL,
                    placedAxis == Direction.Axis.X ? pointDirection.getOpposite() : pointDirection
            );
        }
        throw new AssertionError();
    }

    private Tuple getPropertiesX(final Direction.Axis chainAxis, final Direction.Axis placedAxis, final Direction pointDirection) {
        /* Placed axis can only be X/Z */
        if (chainAxis == Direction.Axis.X) {  // east,west
            return new Tuple(
                    BigChainCornerBlock.ChainDock.HORIZONTAL,
                    placedAxis == Direction.Axis.Y ? pointDirection : pointDirection.getOpposite()
            );
        } else if (chainAxis == Direction.Axis.Z) {  // north,south
            return new Tuple(
                    BigChainCornerBlock.ChainDock.VERTICAL,
                    placedAxis == Direction.Axis.Y ? pointDirection : pointDirection.getOpposite()
            );
        }
        throw new AssertionError();
    }

    private Tuple getPropertiesZ(final Direction.Axis chainAxis, final Direction.Axis placedAxis, final Direction pointDirection) {
        return new Tuple(
                chainAxis == Direction.Axis.X ? BigChainCornerBlock.ChainDock.HORIZONTAL : BigChainCornerBlock.ChainDock.VERTICAL,
                placedAxis == Direction.Axis.Y ? pointDirection.getOpposite() : pointDirection
        );
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

    private static class Tuple {
        public BigChainCornerBlock.ChainDock dock;
        public Direction direction;

        private Tuple(BigChainCornerBlock.ChainDock dock, Direction direction) {
            this.dock = dock;
            if (!direction.getAxis().isHorizontal()) throw new IllegalArgumentException("Direction is not horizontal");
            this.direction = direction;
        }

        public Tuple invert() {
            dock = dock.getOpposite();
            direction = direction.getOpposite();
            return this;
        }
    }
}
