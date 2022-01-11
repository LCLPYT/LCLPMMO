package work.lclpnet.mmo.client;

import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.function.Function;

public class MMOClientHelper {

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, Function<EntityRenderDispatcher, EntityRenderer<T>> factory) {
        EntityRendererRegistry.INSTANCE.register(entityType, (manager, context) -> factory.apply(manager));
    }
}
