package work.lclpnet.mmo.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface UpdateCheckCompleteEvent {

    Event<UpdateCheckCompleteEvent> EVENT = EventFactory.createArrayBacked(UpdateCheckCompleteEvent.class,
            (listeners) -> updateAvailable -> {
                for (UpdateCheckCompleteEvent listener : listeners)
                    listener.onUpdateCheckComplete(updateAvailable);
            });

    void onUpdateCheckComplete(boolean updateAvailable);
}
