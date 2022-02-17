package work.lclpnet.mmo.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenOpenCallback {

    Event<ScreenOpenCallback> EVENT = EventFactory.createArrayBacked(ScreenOpenCallback.class,
            (listeners) -> info -> {
                for (ScreenOpenCallback listener : listeners) {
                    if (info.isCancelled()) break;
                    listener.onScreenOpen(info);
                }
            });

    /**
     * Called, before a new screen is opened on the client.
     *
     * @param screen The event info with the screen to be opened.
     */
    void onScreenOpen(Info screen);

    class Info {
        protected Screen screen;
        protected boolean cancelled = false;

        public Info(Screen screen) {
            this.screen = screen;
        }

        public void setScreen(Screen screen) {
            this.screen = screen;
        }

        public Screen getScreen() {
            return screen;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }
}
