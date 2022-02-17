package work.lclpnet.mmo.util;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mcct.transform.ChunkTransformContext;
import work.lclpnet.mcct.transform.ChunkTransformer;
import work.lclpnet.mcct.transform.IChunkTransformation;

public class SnowLayerFixTransformer implements IChunkTransformation {

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void transform(ChunkTransformContext ctx, ChunkTransformer transformer) {
        CompoundTag root = ctx.getCompound();

        if (!root.contains("Level")) return;
        CompoundTag level = root.getCompound("Level");

        int chunkX = level.getInt("xPos");
        int chunkZ = level.getInt("zPos");

        if (!level.contains("Sections")) return;
        ListTag sections = level.getList("Sections", NbtType.COMPOUND);

        ChunkSection[] chunkSections = new ChunkSection[16];

        for (int i = 0; i < sections.size(); ++i) {
            CompoundTag section = sections.getCompound(i);
            int k = section.getByte("Y");
            if (section.contains("Palette", 9) && section.contains("BlockStates", 12)) {
                ChunkSection chunkSection = new ChunkSection(k << 4);
                chunkSection.getContainer().read(section.getList("Palette", 10), section.getLongArray("BlockStates"));
                chunkSection.calculateCounts();
                if (!chunkSection.isEmpty())
                    chunkSections[k] = chunkSection;
            }
        }

        for (int rx = 0; rx < 16; rx++) {
            for (int rz = 0; rz < 16; rz++) {
                for (int sectionY = 0; sectionY < 16; sectionY++) {
                    boolean sectionDirty = false;

                    for (int ry = 0; ry < 16; ry++) {
                        int y = sectionY * 16 + ry;
                        if (y >= 255) continue; // there cannot be a block above

                        BlockPos pos = new BlockPos(chunkX * 16 + rx, y, chunkZ * 16 + rz);
                        BlockState state = getBlockState(chunkSections, pos);
                        if (!(state.getBlock() instanceof SnowyBlock) || state.get(SnowyBlock.SNOWY)) continue;

                        BlockState above = getBlockState(chunkSections, pos.up());
                        if (!above.isOf(Blocks.SNOW) && !above.isOf(Blocks.SNOW_BLOCK)) continue;

                        setBlockState(chunkSections, pos, state.with(SnowyBlock.SNOWY, true));
                        sectionDirty = true;

                        logger.info("Fixed snowy block at {}", pos);
                    }

                    if (sectionDirty) {
                        if (sectionY >= sections.size()) {
                            logger.warn("Not implemented: Add new section with y={} at chunk ({}, {})", sectionY, chunkX, chunkZ);
                            continue;
                        }

                        CompoundTag sectionTag = sections.getCompound(sectionY + 1);
                        chunkSections[sectionY].getContainer().write(sectionTag, "Palette", "BlockStates");
                        ctx.markDirty();
                    }
                }
            }
        }
    }

    public static BlockState getBlockState(ChunkSection[] chunkSections, BlockPos pos) {
        int i = pos.getY();
        if (World.isOutOfBuildLimitVertically(i)) {
            return Blocks.VOID_AIR.getDefaultState();
        } else {
            ChunkSection chunkSection = chunkSections[i >> 4];
            return ChunkSection.isEmpty(chunkSection) ? Blocks.AIR.getDefaultState() : chunkSection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
        }
    }

    public static void setBlockState(ChunkSection[] chunkSections, BlockPos pos, BlockState state) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (j >= 0 && j < 256) {
            if (chunkSections[j >> 4] != WorldChunk.EMPTY_SECTION || !state.isOf(Blocks.AIR)) {
                ChunkSection chunkSection = getSection(chunkSections, j >> 4);
                chunkSection.setBlockState(i & 15, j & 15, k & 15, state);
            }
        }
    }

    public static ChunkSection getSection(ChunkSection[] chunkSections, int y) {
        if (chunkSections[y] == WorldChunk.EMPTY_SECTION) {
            chunkSections[y] = new ChunkSection(y << 4);
        }

        return chunkSections[y];
    }
}
