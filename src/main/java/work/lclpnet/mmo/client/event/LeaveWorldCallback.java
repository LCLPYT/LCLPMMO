package work.lclpnet.mmo.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

public interface LeaveWorldCallback {

    Event<LeaveWorldCallback> EVENT = EventFactory.createArrayBacked(LeaveWorldCallback.class,
            (listeners) -> world -> {
                for (LeaveWorldCallback listener : listeners)
                    listener.leaveWorld(world);
            });

    /**
     * Called, when a player leaves (unloads) a world.
     *
     * @param world The world that was left.
     */
    void leaveWorld(World world);
}
