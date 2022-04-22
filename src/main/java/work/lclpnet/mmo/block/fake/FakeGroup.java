package work.lclpnet.mmo.block.fake;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mmo.util.NBTHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FakeGroup {

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Relative to origin (model space).
     */
    @Nonnull
    public final Vec3f pivot;
    /**
     * Absolute block position (world space).
     */
    @Nonnull
    public final BlockPos origin;
    @Nonnull
    public final FakeBlock[] blocks;
    @Nullable
    public final FakeGroup[] children;
    @Nullable
    public final Transformation[] transform;

    public FakeGroup(@Nonnull Vec3f pivot, @Nonnull BlockPos origin, @Nonnull FakeBlock[] blocks, @Nullable FakeGroup[] children, @Nullable Transformation[] transform) {
        this.pivot = pivot;
        this.origin = origin;
        this.blocks = blocks;
        this.children = children;
        this.transform = transform;
    }

    public void saveNbt(NbtCompound nbt, BlockPalette blockPalette) {
        nbt.put("Pivot", NBTHelper.serializeVec3f(pivot));
        nbt.putLong("Origin", this.origin.asLong());

        final byte[] blockList = new byte[blocks.length * 5];  // xyz have one byte each, id has two bytes
        for (int i = 0; i < blocks.length; i++) {
            FakeBlock block = blocks[i];
            short paletteId = blockPalette.add(block.getState());

            final int offset = i * 5;
            // encode paletteId
            blockList[offset] = (byte) ((paletteId >> 8) & 0xff);
            blockList[offset + 1] = (byte) (paletteId & 0xff);

            // encode relative position
            final FakeBlockPos relative = block.pos;
            blockList[offset + 2] = relative.x;
            blockList[offset + 3] = relative.y;
            blockList[offset + 4] = relative.z;
        }

        nbt.putByteArray("Blocks", blockList);

        if (children != null) {
            NbtList childrenList = new NbtList();

            for (FakeGroup group : children) {
                NbtCompound compound = new NbtCompound();
                group.saveNbt(compound, blockPalette);
                childrenList.add(compound);
            }

            nbt.put("Children", childrenList);
        }

        if (transform != null) {
            NbtList transformList = new NbtList();

            for (Transformation transformation : transform) {
                NbtCompound transformNbt = new NbtCompound();
                transformation.saveNbt(transformNbt);
                transformList.add(transformNbt);
            }

            nbt.put("Transform", transformList);
        }
    }

    public static FakeGroup loadFromNbt(NbtCompound nbt, BlockPalette blockPalette, World world) {
        NbtElement pivotNbt = nbt.get("Pivot");
        if (!(pivotNbt instanceof NbtList)) return null;

        Vec3f pivot = NBTHelper.deserializeVec3f((NbtList) pivotNbt);
        if (pivot == null) return null;

        if (!(nbt.contains("Origin", NbtType.LONG))) return null;
        final BlockPos origin = BlockPos.fromLong(nbt.getLong("Origin"));

        if (!nbt.contains("Blocks", NbtType.BYTE_ARRAY)) return null;
        final byte[] blockList = nbt.getByteArray("Blocks");

        if (blockList.length % 5 != 0) return null;
        final int blockAmount = blockList.length / 5;

        final FakeBlock[] blocks = new FakeBlock[blockAmount];

        for (int i = 0; i < blockAmount; i++) {
            final int offset = i * 5;

            // first, read identifier
            final byte hi = blockList[offset], lo = blockList[offset + 1];
            short paletteId = (short) ((hi << 8) + lo);

            final BlockState state = blockPalette.get(paletteId);
            if (state == null) {
                LOGGER.warn("Unknown block palette id {}", paletteId);
                continue;
            }

            // then decode relative position
            final FakeBlockPos pos = new FakeBlockPos(
                    blockList[i + 2],
                    blockList[i + 3],
                    blockList[i + 4]
            );

            blocks[i] = new FakeBlock(world, pos, state);
        }

        NbtElement childrenNbt = nbt.get("Children");
        final FakeGroup[] children;
        if (childrenNbt instanceof NbtList childrenList) {
            final int childrenAmount = childrenList.size();
            children = new FakeGroup[childrenAmount];

            for (int i = 0; i < childrenAmount; i++) {
                NbtElement element = childrenList.get(i);
                if (!(element instanceof NbtCompound childNbt)) {
                    LOGGER.warn("Invalid children entry of type {}", element.getType());
                    continue;
                }

                FakeGroup child = FakeGroup.loadFromNbt(childNbt, blockPalette, world);
                if (child == null) {
                    LOGGER.warn("Could not read FakeGroup from NBT");
                    continue;
                }

                children[i] = child;
            }
        } else {
            children = null;
        }

        NbtElement transformNbt = nbt.get("Transform");
        final Transformation[] transform;
        if (transformNbt instanceof NbtList transformList) {
            final int transformCount = transformList.size();
            transform = new Transformation[transformCount];

            for (int i = 0; i < transformCount; i++) {
                NbtElement element = transformList.get(i);
                if (!(element instanceof NbtCompound transformationNbt)) {
                    LOGGER.warn("Invalid transformation entry of type {}", element.getType());
                    continue;
                }

                Transformation transformation = Transformation.loadFromNbt(transformationNbt);
                if (transformation == null) {
                    LOGGER.warn("Could not read Transformation from NBT");
                    continue;
                }

                transform[i] = transformation;
            }
        } else {
            transform = null;
        }

        return new FakeGroup(pivot, origin, blocks, children, transform);
    }
}
