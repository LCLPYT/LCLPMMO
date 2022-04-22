package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.storage.StorageIoWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mixin(StorageIoWorker.class)
public interface StorageIoWorkerAccessor {

    @Invoker("<init>")
    static StorageIoWorker lclpmmo$construct(Path directory, boolean dsync, String name) {
        throw new AssertionError();
    }

    @Invoker("readChunkData")
    CompletableFuture<NbtCompound> lclpmmo$readChunkData(ChunkPos pos);
}
