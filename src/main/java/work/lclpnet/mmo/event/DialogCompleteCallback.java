package work.lclpnet.mmo.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.World;

public interface DialogCompleteCallback {

    Event<DialogCompleteCallback> EVENT = EventFactory.createArrayBacked(DialogCompleteCallback.class,
            (listeners) -> id -> {
                for (DialogCompleteCallback listener : listeners)
                    listener.completeDialog(id);
            });

    /**
     * Called, when a player completes a dialog.
     *
     * @param id The dialog id.
     */
    void completeDialog(int id);
}
