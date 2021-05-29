package work.lclpnet.mmo.event.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Called, when a player clicks an entity.
 * This event is {@link Cancelable}.
 */
@Cancelable
public class EntityRightClickedEvent extends PlayerEvent {

    private final Entity clicked;
    private final boolean client;

    public EntityRightClickedEvent(PlayerEntity player, Entity clicked, boolean client) {
        super(player);
        this.clicked = clicked;
        this.client = client;
    }

    public Entity getClicked() {
        return clicked;
    }

    public boolean isClient() {
        return client;
    }
}
