package work.lclpnet.mmo.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface MMOFirstScreenOpenCallback {

    Event<MMOFirstScreenOpenCallback> EVENT = EventFactory.createArrayBacked(MMOFirstScreenOpenCallback.class,
            (listeners) -> () -> {
                for (MMOFirstScreenOpenCallback listener : listeners) {
                    boolean cancel = listener.beforeFirstScreenOpen();
                    if (cancel) return true;
                }

                return false;
            });

    /**
     * Called, before the first MMO (pre-intro, title) screen is opened.
     *
     * @return Return true, if the first screen should be cancelled. False to open normally.
     */
    boolean beforeFirstScreenOpen();
}
