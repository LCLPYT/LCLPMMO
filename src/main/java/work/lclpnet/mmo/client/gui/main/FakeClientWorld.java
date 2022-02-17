package work.lclpnet.mmo.client.gui.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeAccess;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.asm.mixin.common.DimensionTypeAccessor;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class FakeClientWorld extends ClientWorld {

    public FakeClientWorld(ClientPlayNetworkHandler networkHandler, Properties properties) {
        super(networkHandler, properties,
                RegistryKey.of(Registry.DIMENSION, LCLPMMO.identifier("fakeworld")),
                DimensionTypeAccessor.getOverworld(),
                16,
                MinecraftClient.getInstance()::getProfiler,
                MinecraftClient.getInstance().worldRenderer,
                true,
                BiomeAccess.hashSeed(new Random().nextLong()));
    }
}
