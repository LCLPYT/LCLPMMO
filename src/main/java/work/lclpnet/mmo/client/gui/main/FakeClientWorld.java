package work.lclpnet.mmo.client.gui.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionType;
import work.lclpnet.mmo.LCLPMMO;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class FakeClientWorld extends ClientWorld {

    public FakeClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties) {
        super(networkHandler, properties,
                RegistryKey.of(Registry.WORLD_KEY, LCLPMMO.identifier("fakeworld")),
               getDimensionType(),
                2,
                2,
                MinecraftClient.getInstance()::getProfiler,
                MinecraftClient.getInstance().worldRenderer,
                true,
                BiomeAccess.hashSeed(new Random().nextLong()));
    }

    private static RegistryEntry<DimensionType> getDimensionType() {
        return DynamicRegistryManager.createAndLoad().get(Registry.DIMENSION_TYPE_KEY)
                .getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY);
    }
}
