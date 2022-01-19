package work.lclpnet.mmo;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.example.GeckoLibMod;
import work.lclpnet.mmo.module.BoletusModule;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmo.module.IModule;
import work.lclpnet.mmo.module.PixieModule;
import work.lclpnet.mmo.network.LMNetworking;
import work.lclpnet.mmo.sound.MMOSounds;
import work.lclpnet.mmocontent.util.Env;

import java.util.Set;

public class LCLPMMO implements ModInitializer {

    public static final String MOD_ID = "lclpmmo";
    private static final Logger logger = LogManager.getLogger();

    private static Set<IModule> modules = ImmutableSet.of(
            new DecorationsModule(),
            new PixieModule(),
            new BoletusModule()
    );

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            LCLPMMO.identifier("decorations"),
            () -> new ItemStack(Registry.BLOCK.get(LCLPMMO.identifier("glass_bottle")))
    );

    static {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) GeckoLibMod.DISABLE_IN_DEV = true;
    }

    @Override
    public void onInitialize() {
        Config.load()
                .thenRun(() -> logger.info("Config loaded successfully."))
                .exceptionally(ex -> {
                    logger.error("Config could not be loaded", ex);
                    return null;
                });

        LMNetworking.registerPackets();
        LMNetworking.registerServerPacketHandlers();

        MMOSounds.init();

        modules.forEach(IModule::register);
        modules = null;
    }

    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static Identifier identifier(String format, Object... substitutes) {
        return identifier(String.format(format, substitutes));
    }
}
