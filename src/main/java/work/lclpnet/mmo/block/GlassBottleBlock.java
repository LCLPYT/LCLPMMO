package work.lclpnet.mmo.block;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import work.lclpnet.mmo.blockentity.GlassBottleBlockEntity;
import work.lclpnet.mmo.util.MMOUtils;
import work.lclpnet.mmofurniture.block.FurnitureHorizontalWaterloggedBlock;

public class GlassBottleBlock extends FurnitureHorizontalWaterloggedBlock implements BlockEntityProvider {

    private static final VoxelShape SHAPE = Block.createCuboidShape(4.5D, 0D, 4.5D, 11.5D, 10.5D, 11.5D);
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public GlassBottleBlock() {
        super(Settings.of(Material.DECORATION)
                .strength(0F, 0F)
                .sounds(BlockSoundGroup.GLASS)
                .nonOpaque()
                .noCollision());
        setDefaultState(getDefaultState().with(DIRECTION, Direction.NORTH).with(WATERLOGGED, false).with(ENABLED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(ENABLED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        return direction == Direction.DOWN && !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        return !world.getBlockState(below).getCollisionShape(world, below).getFace(Direction.UP).isEmpty();
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state == null ? null : state.with(ENABLED, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // off hand interaction
        if (hand == Hand.OFF_HAND && !player.getMainHandStack().isEmpty()) return ActionResult.FAIL;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof GlassBottleBlockEntity)) return ActionResult.PASS;

        GlassBottleBlockEntity bottle = (GlassBottleBlockEntity) blockEntity;
        final ItemStack bottleItem = bottle.getItem();
        final ItemStack playerItem = player.getStackInHand(hand);

        if (bottleItem.isEmpty()) { // The bottle block is empty.
            if (playerItem.getItem() instanceof PotionItem) {
                ActionResult result = UseBlockCallback.EVENT.invoker().interact(player, world, hand, hit);
                if (result != ActionResult.PASS) return result;

                if (world.isClient) {
                    return playerItem.getItem() == Items.GLASS_BOTTLE ? ActionResult.PASS : ActionResult.SUCCESS;
                }

                // put the potion inside the bottle block

                ItemStack fluid = playerItem.copy();
                fluid.setCount(1);
                bottle.setItem(fluid);
                player.world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.5F, MMOUtils.randomPitch(world.random, 1.1F, 1.3F));

                if (!player.isCreative()) {
                    playerItem.decrement(1);
                    player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
                }

                return ActionResult.SUCCESS;
            }
        } else if (playerItem.getItem() == Items.GLASS_BOTTLE) {
            // Retrieve potion from bottle block
            ActionResult result = UseBlockCallback.EVENT.invoker().interact(player, world, hand, hit);
            if (result != ActionResult.PASS) return result;

            if (world.isClient) return ActionResult.SUCCESS;

            if (!player.isCreative()) {
                playerItem.decrement(1);
                player.giveItemStack(bottleItem.copy());
            }

            bottle.setItem(ItemStack.EMPTY);
            player.world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.5F, MMOUtils.randomPitch(world.random, 0F, 0.3F));

            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;  // block will be rendered by GlassBottleBlockEntityRenderer, for sodium compat
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GlassBottleBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) return;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof GlassBottleBlockEntity)) return;

        GlassBottleBlockEntity bottle = (GlassBottleBlockEntity) blockEntity;
        ItemStack item = bottle.getItem();
        if (!item.isEmpty()) Block.dropStack(world, pos, item);

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
