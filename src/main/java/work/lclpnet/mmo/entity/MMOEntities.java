package work.lclpnet.mmo.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.MMONames;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@EventBusSubscriber(bus = Bus.MOD, modid = LCLPMMO.MODID)
public class MMOEntities {

    private static final List<EntityType<?>> ENTITY_TYPES = new ArrayList<>();

    public static final EntityType<PixieEntity> PIXIE = register(MMONames.Entity.PIXIE, PixieEntity::new, 0.4F, 0.3F);
    public static final EntityType<BoletusEntity> BOLETUS = register(MMONames.Entity.BOLETUS, BoletusEntity::new, 0.8F, 3F);
    public static final EntityType<FallenKnightEntity> FALLEN_KNIGHT = register(MMONames.Entity.FALLEN_KNIGHT, FallenKnightEntity::new, 1.5F, 5F);
    public static final EntityType<NPCEntity> NPC = register(MMONames.Entity.NPC, NPCEntity::new, 1.5F, 3.5F);

    private static <T extends Entity> EntityType<T> register(String name, Function<World, T> function, float width, float height) {
        EntityType<T> type = EntityType.Builder.<T>create((entityType, world) -> function.apply(world), EntityClassification.CREATURE).size(width, height).setCustomClientFactory((spawnEntity, world) -> function.apply(world)).build(name);
        type.setRegistryName(name);
        ENTITY_TYPES.add(type);
        return type;
    }

    @SubscribeEvent
    public static void registerTypes(final RegistryEvent.Register<EntityType<?>> event) {
        ENTITY_TYPES.forEach(event.getRegistry()::register);
        ENTITY_TYPES.clear();
    }

    @SubscribeEvent
    public static void onRegisterEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(PIXIE, PixieEntity.prepareAttributes().create());
        event.put(BOLETUS, BoletusEntity.prepareAttributes().create());
        event.put(FALLEN_KNIGHT, FallenKnightEntity.prepareAttributes().create());
    }
}
