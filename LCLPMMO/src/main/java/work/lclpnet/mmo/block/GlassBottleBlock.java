package work.lclpnet.mmo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import work.lclpnet.mmo.event.custom.GlassBottleEvent;
import work.lclpnet.mmo.tileentity.GlassBottleTileEntity;
import work.lclpnet.mmo.util.ItemStackHelper;
import work.lclpnet.mmo.util.SoundHelper;

@SuppressWarnings("deprecation")
public class GlassBottleBlock extends MMOHorizontalWaterloggableBlock implements ITileEntityProvider{

	private static final VoxelShape SHAPE = Block.makeCuboidShape(3.5D, 0D, 3.5D, 12.5D, 13D, 12.5D);
	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

	public GlassBottleBlock(Properties properties) {
		super(properties);
		setDefaultState(this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH).with(WATERLOGGED, false).with(ENABLED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(ENABLED);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(ENABLED, false);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult hit) {
		TileEntity tileentity = worldIn.getTileEntity(pos);
		if(!(tileentity instanceof GlassBottleTileEntity)) return ActionResultType.PASS;

		ItemStack heldItem = player.getHeldItem(hand);
		
		GlassBottleTileEntity bottle = (GlassBottleTileEntity) tileentity;
		if(ItemStackHelper.isAir(bottle.getItem())) {
			//The bottle is empty.
			if(offHandCheck(player, hand)) return ActionResultType.FAIL;
			
			if(ItemStackHelper.isPotion(heldItem)) {
				//Add to bottle
				GlassBottleEvent.Fill event = new GlassBottleEvent.Fill(worldIn, pos, state, player, heldItem);
				MinecraftForge.EVENT_BUS.post(event);
				if(event.isCanceled()) return ActionResultType.FAIL;
				
				ItemStack fluid = event.getItem().copy();
				fluid.setCount(1);
				bottle.setItem(fluid);
				player.world.playSound(player, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.5F, SoundHelper.randomPitch(1.1F, 1.3F));
				if(!player.isCreative()) {
					heldItem.shrink(1);
					player.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
				}
				return ActionResultType.SUCCESS;
			}
		} else {
			//Something is inside the bottle.
			if(offHandCheck(player, hand)) return ActionResultType.FAIL;
			
			if(ItemStackHelper.isItem(heldItem, Items.GLASS_BOTTLE)) {
				//Retrieve from bottle
				GlassBottleEvent.Empty event = new GlassBottleEvent.Empty(worldIn, pos, state, player, bottle.getItem());
				MinecraftForge.EVENT_BUS.post(event);
				if(event.isCanceled()) return ActionResultType.FAIL;
				
				player.world.playSound(player, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 0.5F, SoundHelper.randomPitch(0F, 0.3F));
				if(!player.isCreative()) {
					heldItem.shrink(1);
					player.addItemStackToInventory(event.getItem());
				}
				bottle.setItem(ItemStack.EMPTY);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	/**
	 * @return True, if the interaction was made with off_hand and if the main_hand item is something.
	 */
	private boolean offHandCheck(PlayerEntity player, Hand hand) {
		return hand == Hand.OFF_HAND && !ItemStackHelper.isAir(player.getHeldItemMainhand());
	}

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
		return false;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return SHAPE;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if(!state.isValidPosition(worldIn, pos)) {
			worldIn.playEvent(2001, pos, Block.getStateId(worldIn.getBlockState(pos)));
			spawnDrops(state, worldIn, pos);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
	}

	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return !state.getCollisionShape(worldIn, pos).project(Direction.UP).isEmpty();
	}

	@Override
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new GlassBottleTileEntity();
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new GlassBottleTileEntity();
	}

}
