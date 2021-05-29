package work.lclpnet.mmo;

import com.electronwill.nightconfig.core.file.FileConfig;
import work.lclpnet.mmo.util.Holder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static FileConfig config = null;
    private static final Map<String, Object> register = new HashMap<>();

    public static final String KEY_SKIP_INTRO = "misc.skip-intro",
            KEY_DISCORD_INTEGRATION = "misc.enable-discord-integration",
            KEY_NETWORK_STAGING = "network.staging",
            KEY_NETWORK_HOST_STAGING = "network.host-staging",
            KEY_NETWORK_HOST_LIVE = "network.host-live",
            KEY_MINECRAFT_MUSIC_DISABLED = "game.disable-minecraft-music",
            KEY_DEBUG_CAPE = "debug.cape";

    static {
        register.put(KEY_SKIP_INTRO, false);
        register.put(KEY_DISCORD_INTEGRATION, true);
        register.put(KEY_NETWORK_STAGING, false);
        register.put(KEY_NETWORK_HOST_STAGING, "http://localhost:8000");
        register.put(KEY_NETWORK_HOST_LIVE, "https://lclpnet.work");
        register.put(KEY_MINECRAFT_MUSIC_DISABLED, false);
        register.put(KEY_DEBUG_CAPE, false);
    }

    public static void load() {
        File configDir = new File("config");
        File configFile = new File(configDir, "lclpmmo.toml");

        if (!configFile.exists()) {
            try {
                createConfigFile(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = FileConfig.builder(configFile).build();
        config.load();
        config.close();

        populateConfig();
    }

    private static void populateConfig() {
        Holder<Boolean> modified = new Holder<>(false);

        register.forEach((path, defaultValue) -> {
            if (!config.contains(path)) {
                config.set(path, defaultValue);
                if (!modified.value) modified.value = true;
            }
        });

        if (modified.value) save();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void createConfigFile(File config) throws IOException {
        config.getParentFile().mkdirs();
        config.createNewFile();
    }

    private static void set(String path, Object val) {
        config.set(path, val);
        save();
    }

    private static <T> T get(String path) {
        ensureConfigNonNull();
        if (!config.contains(path)) {
            if (!register.containsKey(path)) throw new IllegalStateException("Path not registered.");
            set(path, register.get(path));
        }
        return config.get(path);
    }

    private static void ensureConfigNonNull() {
        if (config == null) load();
    }

    private static void save() {
        new Thread(() -> {
            FileConfig newConfig = FileConfig.builder(config.getFile()).build();
            newConfig.putAll(config);
            newConfig.save();
            newConfig.close();
        }, "Config Saver").start();
    }

    public static boolean shouldSkipIntro() {
        return get(KEY_SKIP_INTRO);
    }

    public static boolean enableDiscordIntegration() {
        return get(KEY_DISCORD_INTEGRATION);
    }

    public static boolean isNetworkStagingMode() {
        return get(KEY_NETWORK_STAGING);
    }

    public static String getHostStaging() {
        return get(KEY_NETWORK_HOST_STAGING);
    }

    public static String getHostLive() {
        return get(KEY_NETWORK_HOST_LIVE);
    }

    public static String getEffectiveHost() {
        return isNetworkStagingMode() ? getHostStaging() : getHostLive();
    }

    public static void setMinecraftMusicDisabled(boolean disabled) {
        set(KEY_MINECRAFT_MUSIC_DISABLED, disabled);
    }

    public static boolean isMinecraftMusicDisabled() {
        return get(KEY_MINECRAFT_MUSIC_DISABLED);
    }

    public static boolean isDebugCape() {
        return get(KEY_DEBUG_CAPE);
    }
}
