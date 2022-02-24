package work.lclpnet.mmo.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import work.lclpnet.mmofurniture.block.FurnitureHorizontalWaterloggedBlock;

public class BigChainCornerBlock extends FurnitureHorizontalWaterloggedBlock implements IBigChainBlock {

    public static final BooleanProperty UP = Properties.UP;
    public static final EnumProperty<ChainDock> DOCK = EnumProperty.of("dock", ChainDock.class);
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    public BigChainBlock baseBlock = null;

    public BigChainCornerBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.GRAY)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, 2)
                .nonOpaque()
                .strength(5.0F, 6.0F)
                .sounds(BlockSoundGroup.METAL));

        setDefaultState(getDefaultState()
                .with(UP, false)
                .with(DOCK, ChainDock.HORIZONTAL)
                .with(AXIS, Direction.Axis.Y));
    }

    // TODO consider shape

    @Override
    public BlockItem provideBlockItem(Item.Settings settings) {
        return null;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(baseBlock);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(UP, DOCK, AXIS);
    }

    public enum ChainDock implements StringIdentifiable {
        VERTICAL("vertical"),
        HORIZONTAL("horizontal");

        private final String name;

        ChainDock(String name) {
            this.name = name;
        }

        ChainDock getOpposite() {
            return this == VERTICAL ? HORIZONTAL : VERTICAL;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
