package work.lclpnet.mmo;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.mcct.transform.ChunkTransformContext;
import work.lclpnet.mcct.transform.IChunkTransformer;
import work.lclpnet.mcct.transform.MCCT;
import work.lclpnet.mmo.module.BoletusModule;
import work.lclpnet.mmo.module.DecorationsModule;
import work.lclpnet.mmo.module.IModule;
import work.lclpnet.mmo.module.PixieModule;
import work.lclpnet.mmo.network.LMNetworking;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;
import work.lclpnet.mmo.sound.MMOSounds;

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

        LCLPNetworkSession.startup();

        MCCT.registerTransformer(ctx -> {
            CompoundTag chunkData = ctx.getCompound();
            if (!chunkData.contains("Level", NbtType.COMPOUND)) return;

            CompoundTag level = chunkData.getCompound("Level");
            if (!level.contains("Sections", NbtType.LIST)) return;

            ListTag sections = level.getList("Sections", NbtType.COMPOUND);
            sections.forEach(sectionTag -> {
                if (!(sectionTag instanceof CompoundTag)) return;

                CompoundTag section = (CompoundTag) sectionTag;
                if (!section.contains("Palette", NbtType.LIST)) return;

                ListTag palette = section.getList("Palette", NbtType.COMPOUND);
                palette.forEach(paletteEntryTag -> {
                    if (!(paletteEntryTag instanceof CompoundTag)) return;

                    CompoundTag paletteEntry = (CompoundTag) paletteEntryTag;
                    if (!paletteEntry.contains("Name", NbtType.STRING)) return;

                    String blockId = paletteEntry.getString("Name");
                    if (!"minecraft:diamond_block".equals(blockId)) return;

                    paletteEntry.putString("Name", "minecraft:gold_block");
                    ctx.markDirty();
                });
            });
        });
    }

    public static Identifier identifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static Identifier identifier(String format, Object... substitutes) {
        return identifier(String.format(format, substitutes));
    }
}
