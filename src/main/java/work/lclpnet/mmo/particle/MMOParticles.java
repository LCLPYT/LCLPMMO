package work.lclpnet.mmo.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.MMONames;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = LCLPMMO.MODID)
public class MMOParticles {

    private static final List<ParticleType<?>> PARTICLES = new ArrayList<>();

    public static final BasicParticleType SPORES = register(MMONames.Particle.SPORES, false);

    private static BasicParticleType register(String name, boolean alwaysShow) {
        BasicParticleType type = new BasicParticleType(alwaysShow);
        type.setRegistryName(name);
        PARTICLES.add(type);
        return type;
    }

    @SubscribeEvent
    public static void onRegisterParticle(RegistryEvent.Register<ParticleType<?>> event) {
        PARTICLES.forEach(event.getRegistry()::register);
    }

    @SubscribeEvent
    public static void onRegisterParticle(ParticleFactoryRegisterEvent e) {
        ParticleManager particles = Minecraft.getInstance().particles;
        particles.registerFactory(SPORES, SporeParticle.Factory::new);
    }

}
