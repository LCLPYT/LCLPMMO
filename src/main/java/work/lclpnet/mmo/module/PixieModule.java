package work.lclpnet.mmo.module;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.PixieEntity;
import work.lclpnet.mmocontent.entity.MMOEntityAttributes;
import work.lclpnet.mmocontent.item.MMOItemRegistrar;

public class PixieModule implements IModule {

    public static EntityType<PixieEntity> pixieEntityType;

    @Override
    public void register() {
        pixieEntityType = Registry.register(
                Registry.ENTITY_TYPE,
                LCLPMMO.identifier("pixie"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<PixieEntity>) PixieEntity::new)
                        .dimensions(EntityDimensions.fixed(0.4F, 0.3F))
                        .trackRangeChunks(8)
                        .build()
        );

        MMOEntityAttributes.registerDefaultAttributes(pixieEntityType, PixieEntity.createMobAttributes());

        MMOItemRegistrar.registerSpawnEgg(pixieEntityType, "pixie", 0x7dafff, 0xdeebff, LCLPMMO::identifier);
    }
}
