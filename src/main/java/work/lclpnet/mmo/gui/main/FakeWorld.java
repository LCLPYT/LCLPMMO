package work.lclpnet.mmo.gui.main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.LCLPMMO;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FakeWorld extends ClientWorld {

    public FakeWorld(ClientPlayNetHandler handler, ClientWorldInfo worldInfo) {
        super(handler,
                worldInfo,
                RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(LCLPMMO.MODID, "fakeworld")),
                DimensionType.OVERWORLD_TYPE,
                16, // render distance
                Minecraft.getInstance()::getProfiler,
                Minecraft.getInstance().worldRenderer,
                true, // isDebugWorld
                BiomeManager.getHashedSeed(new Random().nextLong()) // first 8 bytes of the sha-256 of the world's seed (in this case a dummy seed)
        );
    }
}
