package work.lclpnet.mmo.block.fake;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FakeStructure {

    public final UUID uuid;
    @Nonnull
    public final FakeGroup[] groups;

    public FakeStructure(UUID uuid, @Nonnull FakeGroup[] groups) {
        this.uuid = Objects.requireNonNull(uuid);
        this.groups = Objects.requireNonNull(groups);
    }

    public void saveNbt(NbtCompound nbt) {
        nbt.putUuid("UUID", this.uuid);

        BlockPalette blockPalette = new BlockPalette();

        NbtList groupList = new NbtList();
        for (FakeGroup group : groups) {
            NbtCompound groupNbt = new NbtCompound();
            group.saveNbt(groupNbt, blockPalette);
            groupList.add(groupNbt);
        }

        blockPalette.saveNbt(nbt);
        nbt.put("Groups", groupList);
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private static Optional<FakeStructure> loadFromNbt(NbtCompound nbt, World world) {
        UUID uuid = nbt.getUuid("UUID");
        if (uuid == null) return Optional.empty();

        BlockPalette blockPalette = BlockPalette.loadFromNbt(nbt);
        if (blockPalette == null) blockPalette = new BlockPalette();

        NbtElement groupsNbt = nbt.get("Groups");
        if (!(groupsNbt instanceof NbtList groupsList)) return Optional.empty();

        FakeGroup[] groups = new FakeGroup[groupsList.size()];

        for (int i = 0; i < groupsList.size(); i++) {
            NbtElement element = groupsList.get(i);
            if (!(element instanceof NbtCompound groupNbt)) {
                LOGGER.warn("Invalid group entry of type {}", element.getType());
                continue;
            }

            FakeGroup group = FakeGroup.loadFromNbt(groupNbt, blockPalette, world);
            if (group == null) {
                LOGGER.warn("Could not load FakeGroup");
                continue;
            }

            groups[i] = group;
        }

        return Optional.of(new FakeStructure(uuid, groups));
    }

    public static void loadFromNbt(NbtCompound nbtCompound, World world, Function<FakeStructure, FakeStructure> consumer) {
        try {
            FakeStructure.loadFromNbt(nbtCompound, world).map(consumer);
        } catch (RuntimeException runtimeException) {
            LOGGER.warn("Exception loading entity: ", runtimeException);
        }
    }

    public static Stream<FakeStructure> streamFromNbt(final NbtList list, final World world) {
        final Spliterator<? extends NbtElement> spliterator = list.spliterator();
        return StreamSupport.stream(new Spliterator<>() {
            @Override
            public boolean tryAdvance(Consumer<? super FakeStructure> action) {
                return spliterator.tryAdvance(nbtElement -> FakeStructure.loadFromNbt((NbtCompound) nbtElement, world, entity -> {
                    action.accept(entity);
                    return entity;
                }));
            }

            @Override
            public Spliterator<FakeStructure> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return list.size();
            }

            @Override
            public int characteristics() {
                return Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE;
            }
        }, false);
    }
}
