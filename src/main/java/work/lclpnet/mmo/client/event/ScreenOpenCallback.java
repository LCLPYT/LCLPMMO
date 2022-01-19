package work.lclpnet.mmo.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenOpenCallback {

    Event<ScreenOpenCallback> EVENT = EventFactory.createArrayBacked(ScreenOpenCallback.class,
            (listeners) -> screen -> {
                for (ScreenOpenCallback listener : listeners) {
                    Screen result = listener.onScreenOpen(screen);
                    if (result != null) return result;
                }

                return null;
            });

    /**
     * Called, before a new screen is opened on the client.
     *
     * @param screen The screen to be opened.
     * @return Return a screen that should be opened instead, or null, to open the original screen
     */
    Screen onScreenOpen(Screen screen);
}
