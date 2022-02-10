package work.lclpnet.mmo.migrator;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mmo.client.gui.WorldMigrationScreen;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class WorldMigrator {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final LevelInfo levelInfo;
    protected final LevelStorage.Session session;
    protected final ImmutableSet<RegistryKey<World>> worlds;

    protected WorldMigrator(LevelStorage.Session session, LevelInfo levelInfo, ImmutableSet<RegistryKey<World>> worlds) {
        this.session = Objects.requireNonNull(session);
        this.levelInfo = Objects.requireNonNull(levelInfo);
        this.worlds = Objects.requireNonNull(worlds);
    }

    public static CompletableFuture<WorldMigrator> create(MinecraftClient client, LevelStorage.Session storageSession) {
        return CompletableFuture.supplyAsync(() -> createSync(client, storageSession));
    }

    protected static WorldMigrator createSync(MinecraftClient client, LevelStorage.Session storageSession) {
        DynamicRegistryManager.Impl impl = DynamicRegistryManager.create();

        try {
            MinecraftClient.IntegratedResourceManager integratedResourceManager = client.method_29604(impl, MinecraftClient::method_29598, MinecraftClient::createSaveProperties, false, storageSession);

            WorldMigrator migrator;
            try {
                SaveProperties saveProperties = integratedResourceManager.getSaveProperties();
                storageSession.backupLevelDataFile(impl, saveProperties);
                ImmutableSet<RegistryKey<World>> immutableSet = saveProperties.getGeneratorOptions().getWorlds();
                migrator = new WorldMigrator(storageSession, saveProperties.getLevelInfo(), immutableSet);
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
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
