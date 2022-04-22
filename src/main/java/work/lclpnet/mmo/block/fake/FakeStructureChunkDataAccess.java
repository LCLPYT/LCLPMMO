package work.lclpnet.mmo.block.fake;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.storage.ChunkDataAccess;
import net.minecraft.world.storage.ChunkDataList;
import net.minecraft.world.storage.StorageIoWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mmo.asm.mixin.common.StorageIoWorkerAccessor;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class FakeStructureChunkDataAccess implements ChunkDataAccess<FakeStructure> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FAKE_STRUCTURES_KEY = "FakeStructures";
    private static final String POSITION_KEY = "Position";
    private final ServerWorld world;
    private final StorageIoWorker dataLoadWorker;
    private final LongSet emptyChunks = new LongOpenHashSet();
    private final TaskExecutor<Runnable> taskExecutor;
    protected final DataFixer dataFixer;

    public FakeStructureChunkDataAccess(ServerWorld world, Path path, DataFixer dataFixer, boolean dsync, Executor executor) {
        this.world = world;
        this.dataFixer = dataFixer;
        this.taskExecutor = TaskExecutor.create(executor, "entity-deserializer");
        this.dataLoadWorker = StorageIoWorkerAccessor.lclpmmo$construct(path, dsync, "fakestructures");
    }

    @Override
    public CompletableFuture<ChunkDataList<FakeStructure>> readChunkData(ChunkPos pos) {
        if (this.emptyChunks.contains(pos.toLong())) {
            return CompletableFuture.completedFuture(FakeStructureChunkDataAccess.emptyDataList(pos));
        }

        StorageIoWorkerAccessor accessor = (StorageIoWorkerAccessor) this.dataLoadWorker;

        return accessor.lclpmmo$readChunkData(pos).thenApplyAsync(compound -> {
            if (compound == null) {
                this.emptyChunks.add(pos.toLong());
                return FakeStructureChunkDataAccess.emptyDataList(pos);
            }
            try {
                ChunkPos chunkPos2 = FakeStructureChunkDataAccess.getChunkPos(compound);
                if (!Objects.equals(pos, chunkPos2)) {
                    LOGGER.error("Chunk file at {} is in the wrong location. (Expected {}, got {})", (Object)pos, (Object)pos, (Object)chunkPos2);
                }
            } catch (Exception exception) {
                LOGGER.warn("Failed to parse chunk {} position info: {}", pos, exception);
            }
            NbtList nbtList = compound.getList(FAKE_STRUCTURES_KEY, 10);
            List<FakeStructure> list = FakeStructure.streamFromNbt(nbtList, this.world).collect(ImmutableList.toImmutableList());
            return new ChunkDataList<>(pos, list);
        }, this.taskExecutor::send);
    }

    @Override
    public void writeChunkData(ChunkDataList<FakeStructure> dataList) {
        ChunkPos chunkPos = dataList.getChunkPos();
        if (dataList.isEmpty()) {
            if (this.emptyChunks.add(chunkPos.toLong())) {
                this.dataLoadWorker.setResult(chunkPos, null);
            }
            return;
        }
        NbtList nbtList = new NbtList();
        dataList.stream().forEach(fakeStructure -> {
            NbtCompound nbtCompound = new NbtCompound();
            fakeStructure.saveNbt(nbtCompound);
            nbtList.add(nbtCompound);
        });
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        nbtCompound.put(FAKE_STRUCTURES_KEY, nbtList);
        FakeStructureChunkDataAccess.putChunkPos(nbtCompound, chunkPos);
        this.dataLoadWorker.setResult(chunkPos, nbtCompound).exceptionally(ex -> {
            LOGGER.error("Failed to store chunk {}: {}", chunkPos, ex);
            return null;
        });
        this.emptyChunks.remove(chunkPos.toLong());
    }

    @Override
    public void awaitAll(boolean sync) {
        this.dataLoadWorker.completeAll(sync).join();
        this.taskExecutor.awaitAll();
    }

    private static ChunkPos getChunkPos(NbtCompound chunkNbt) {
        int[] is = chunkNbt.getIntArray(POSITION_KEY);
        return new ChunkPos(is[0], is[1]);
    }

    private static void putChunkPos(NbtCompound chunkNbt, ChunkPos pos) {
        chunkNbt.put(POSITION_KEY, new NbtIntArray(new int[]{pos.x, pos.z}));
    }

    private static ChunkDataList<FakeStructure> emptyDataList(ChunkPos pos) {
        return new ChunkDataList<FakeStructure>(pos, ImmutableList.of());
    }
}
