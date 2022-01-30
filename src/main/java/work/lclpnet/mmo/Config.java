package work.lclpnet.mmo;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Config {

    public final Misc misc = new Misc();
    public final Networking networking = new Networking();
    public final Game game = new Game();
    public final Debug debug = new Debug();

    protected transient final Computed _computed = new Computed();

    public static class Misc {
        public boolean skipIntro = false,
                discordIntegration = true;
    }

    public static class Networking {
        public String selectedProvider = "production";
        public Map<String, String> providers = ImmutableMap.of(
                "production", "https://lclpnet.work",
                "staging", "https://staging.lclpnet.work",
                "dev", "http://localhost:8000"
        );
    }

    public static class Game {
        public boolean disableMinecraftMusic = false;
    }

    public static class Debug {
        public boolean cape = false;
    }

    private static class Computed {
        public String effectiveHost = null;
    }

    // IO logic

    private static Config config = null;
    private static final Logger logger = LogManager.getLogger();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @NotNull
    protected static File getConfigFile() {
        return new File("config", "lclpmmo.json");
    }

    public static CompletableFuture<Void> load() {
        return CompletableFuture.runAsync(() -> {
            File configFile = getConfigFile();

            if (!configFile.exists()) {
                config = new Config(); // default config
                save(); // do not chain save, this can be done separately
                return;
            }

            try (JsonReader reader = new JsonReader(new FileReader(configFile))) {
                config = gson.fromJson(reader, Config.class);
                if (config == null) config = new Config(); // default config
            } catch (Exception e) {
                logger.error("Could not load config", e);
                config = new Config(); // default config
            }
        });
    }

    public static CompletableFuture<Void> save() {
        return CompletableFuture.runAsync(() -> {
            if (config == null) throw new IllegalStateException("Tried to save null config");

            File configFile = getConfigFile();

            if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
                logger.error("Could not create config directory.");
                return;
            }

            try (JsonWriter writer = gson.newJsonWriter(new FileWriter(configFile))) {
                JsonElement json = gson.toJsonTree(config);
                gson.toJson(json, writer);
            } catch (Exception e) {
                logger.error("Could not write config file", e);
            }
        });
    }

    public static void dispatchHandledSave() {
        save().exceptionally(ex -> {
            logger.error("Failed to save config", ex);
            return null;
        });
    }

    /* - */

    public static boolean shouldSkipIntro() {
        return config.misc.skipIntro;
    }

    public static boolean isMinecraftMusicDisabled() {
        return config.game.disableMinecraftMusic;
    }

    public static void setMinecraftMusicDisabled(boolean disable) {
        if (config.game.disableMinecraftMusic != disable) {
            config.game.disableMinecraftMusic = disable;
            dispatchHandledSave();
        }
    }

    public static String getNetworkingProvider() {
        return config.networking.selectedProvider;
    }

    public static String getEffectiveHost() {
        if (config._computed.effectiveHost == null) {
            String provider = config.networking.selectedProvider;
            if (provider == null) throw new IllegalStateException("networking.provider must be set in the config");

            Map<String, String> providers = config.networking.providers;
            if (providers == null) throw new IllegalStateException("networking.providers must be set in the config");

            String host = providers.get(provider);
            if (host == null) throw new IllegalStateException("networking.provider is not mapped by networking.providers in the config");

            config._computed.effectiveHost = host;
        }

        return config._computed.effectiveHost;
    }
}
