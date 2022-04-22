package work.lclpnet.mmo.block.fake;

import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class BlockPalette {

    protected final Short2ObjectAVLTreeMap<BlockState> palette;

    public BlockPalette() {
        palette = new Short2ObjectAVLTreeMap<>();
        palette.defaultReturnValue(Blocks.AIR.getDefaultState());
    }

    public short add(BlockState state) {
        final int unsafeId = palette.isEmpty() ? 0 : palette.lastShortKey() + 1;
        if (unsafeId > Short.MAX_VALUE) throw new IllegalStateException("Maximum number of mappings has been reached.");

        short id = (short) unsafeId;
        palette.put(id, state);
        return id;
    }

    public BlockState get(short id) {
        return palette.get(id);
    }

    public void saveNbt(NbtCompound nbt) {
        NbtList list = new NbtList();

        for (BlockState entry : palette.values()) {
            list.add(NbtHelper.fromBlockState(entry));
        }

        nbt.put("Palette", list);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static BlockPalette loadFromNbt(NbtCompound nbt) {
        NbtElement paletteNbt = nbt.get("Palette");
        if (!(paletteNbt instanceof NbtList list)) return null;

        BlockPalette palette = new BlockPalette();
        for (NbtElement element : list) {
            if (!(element instanceof NbtCompound entryNbt)) {
                LOGGER.warn("Invalid palette entry of type {}", element.getType());
                continue;
            }

            BlockState state = NbtHelper.toBlockState(entryNbt);
            if (state != null)
                palette.add(state);
        }

        return palette;
    }
}
