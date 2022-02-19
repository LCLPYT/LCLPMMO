package work.lclpnet.mmo.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.StringIdentifiable;
import work.lclpnet.mmofurniture.block.FurnitureHorizontalWaterloggedBlock;

public class BigChainCornerBlock extends FurnitureHorizontalWaterloggedBlock {

    public static final EnumProperty<BlockHalf> DIRECTION = Properties.BLOCK_HALF;
    public static final EnumProperty<ChainDock> DOCK = EnumProperty.of("dock", ChainDock.class);

    public BigChainCornerBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.GRAY)
                .requiresTool()
                .breakByTool(FabricToolTags.PICKAXES, 2)
                .nonOpaque()
                .strength(5.0F, 6.0F)
                .sounds(BlockSoundGroup.METAL));

        setDefaultState(getDefaultState()
                .with(DIRECTION, BlockHalf.TOP)
                .with(DOCK, ChainDock.HORIZONTAL));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(DIRECTION, DOCK);
    }

    public enum ChainDock implements StringIdentifiable {
        VERTICAL("vertical"),
        HORIZONTAL("horizontal");

        private final String name;

        private ChainDock(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}
