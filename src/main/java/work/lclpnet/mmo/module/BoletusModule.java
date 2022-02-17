package work.lclpnet.mmo.module;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.BoletusEntity;
import work.lclpnet.mmocontent.entity.MMOEntityAttributes;
import work.lclpnet.mmocontent.item.MMOItemRegistrar;

public class BoletusModule implements IModule {

    public static EntityType<BoletusEntity> boletusEntityType;
    public static final DefaultParticleType sporesParticleType = FabricParticleTypes.simple();

    @Override
    public void register() {
        boletusEntityType = Registry.register(
                Registry.ENTITY_TYPE,
                LCLPMMO.identifier("boletus"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<BoletusEntity>) BoletusEntity::new)
                        .dimensions(EntityDimensions.fixed(0.8F, 3F))
                        .trackRangeChunks(8)
                        .build()
        );

        MMOEntityAttributes.registerDefaultAttributes(boletusEntityType, BoletusEntity.createMobAttributes());

        MMOItemRegistrar.registerSpawnEgg(boletusEntityType, "boletus", 0x6d8c4a, 0x993a29, LCLPMMO::identifier);

        Registry.register(Registry.PARTICLE_TYPE, LCLPMMO.identifier("spores"), sporesParticleType);
    }
}
