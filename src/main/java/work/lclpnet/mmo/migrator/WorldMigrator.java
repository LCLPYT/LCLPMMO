package work.lclpnet.mmo.migrator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.storage.RegionFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldMigrator {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");

    protected final LevelInfo levelInfo;
    protected final LevelStorage.Session session;
    protected final ImmutableSet<RegistryKey<World>> worlds;
    protected final ProgressListener progressListener;

    protected WorldMigrator(LevelStorage.Session session, LevelInfo levelInfo, ImmutableSet<RegistryKey<World>> worlds, ProgressListener progressListener) {
        this.session = Objects.requireNonNull(session);
        this.levelInfo = Objects.requireNonNull(levelInfo);
        this.worlds = Objects.requireNonNull(worlds);
        this.progressListener = Objects.requireNonNull(progressListener);
        this.progressListener.setSteps(worlds.size());
    }

    public static CompletableFuture<WorldMigrator> create(MinecraftClient client, LevelStorage.Session storageSession, ProgressListener progressListener) {
        return CompletableFuture.supplyAsync(() -> createSync(client, storageSession, progressListener));
    }

    protected static WorldMigrator createSync(MinecraftClient client, LevelStorage.Session storageSession, ProgressListener progressListener) {
        DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();

        try {
            MinecraftClient.IntegratedResourceManager integratedResourceManager = client.method_29604(impl, MinecraftClient::method_29598, MinecraftClient::createSaveProperties, false, storageSession);

            WorldMigrator migrator;
            try {
                SaveProperties saveProperties = integratedResourceManager.getSaveProperties();
                storageSession.backupLevelDataFile(impl, saveProperties);
                ImmutableSet<RegistryKey<World>> immutableSet = saveProperties.getGeneratorOptions().getWorlds();
                migrator = new WorldMigrator(storageSession, saveProperties.getLevelInfo(), immutableSet, progressListener);
            } finally {
                if (integratedResourceManager != null) {
                    integratedResourceManager.close();
                }
            }

            return migrator;
        } catch (Exception e) {
            LOGGER.warn("Failed to load datapacks, can't optimize world", e);
            return null;
        }
    }

    public CompletableFuture<Void> migrate() {
        return CompletableFuture.runAsync(this::migrateSync);
    }

    protected void migrateSync() {
        final ImmutableList<RegistryKey<World>> list = this.worlds.asList();

        final int dimCount = worlds.size();
        LOGGER.info("Found {} dimensions", dimCount);

        for (int i = 0; i < dimCount; i++) {
            this.progressListener.updateCurrentStep(i + 1);
            this.migrateWorld(list.get(i));
        }

        LOGGER.info("Migration complete.");
    }

    protected void migrateWorld(RegistryKey<World> world) {
        LOGGER.info("Migrating world {}...", world.getValue());

        File worldDirectory = this.session.getWorldDirectory(world);
        File regionDirectory = new File(worldDirectory, "region");
        File[] files = regionDirectory.listFiles((filex, string) -> string.endsWith(".mca"));
        if (files == null) {
            LOGGER.info("No region files found in {}", regionDirectory.getAbsolutePath());
            return;
        }

        List<RegionFileLocation> regionFiles = new ArrayList<>();

        for (File regionFile : files) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(regionFile.getName());
            if (!matcher.matches()) continue;

            int regionX = Integer.parseInt(matcher.group(1)) << 5;
            int regionY = Integer.parseInt(matcher.group(2)) << 5;

            regionFiles.add(new RegionFileLocation(regionFile, regionX, regionY));
        }

        final int regionFileCount = regionFiles.size();
        LOGGER.info("Found {} region files.", regionFileCount);

        for (int i = 0; i < regionFileCount; i++) {
            RegionFileLocation regionFile = regionFiles.get(i);
            migrateRegionFile(regionFile, regionDirectory);
            this.progressListener.updateProgress((i + 1) / (float) regionFileCount);
        }

        LOGGER.info("World {} migrated successfully.", world.getValue());
    }

    protected void migrateRegionFile(RegionFileLocation region, File regDirectory) {
        LOGGER.info("Migrating region file {}...", region.file.getName());

        try (RegionFile regionFile = new RegionFile(region.file, regDirectory, true)) {
            List<ChunkPos> chunkPositions = Lists.newArrayList();

            for (int x = 0; x < 32; ++x) {
                for (int y = 0; y < 32; ++y) {
                    ChunkPos chunkPos = new ChunkPos(x + region.x, y + region.y);

                    if (regionFile.isChunkValid(chunkPos))
                        chunkPositions.add(chunkPos);
                }
            }

            for (ChunkPos chunkPosition : chunkPositions) {
                migrateChunk(regionFile, chunkPosition);
            }
        } catch (Throwable ignored) {}
    }

    protected void migrateChunk(RegionFile regionFile, ChunkPos chunkPos) throws IOException {
        CompoundTag chunkTag;
        try (DataInputStream chunkIn = regionFile.getChunkInputStream(chunkPos)) {
            if (chunkIn == null) {
                LOGGER.info("Failed to fetch input stream for chunk {}", chunkPos);
                return;
            }

            chunkTag = NbtIo.read(chunkIn);
        }

        migrateChunkTag(chunkTag);

        try (DataOutputStream chunkOut = regionFile.getChunkOutputStream(chunkPos)) {
            NbtIo.write(chunkTag, chunkOut);
        }
    }

    protected void migrateChunkTag(CompoundTag chunkTag) {

    }

    public static class RegionFileLocation {
        public final File file;
        public final int x, y;

        public RegionFileLocation(File file, int x, int y) {
            this.file = file;
            this.x = x;
            this.y = y;
        }
    }

    public interface ProgressListener {
        void setSteps(int steps);
        void updateCurrentStep(int currentStep);
        void updateProgress(float progress);
    }
}
