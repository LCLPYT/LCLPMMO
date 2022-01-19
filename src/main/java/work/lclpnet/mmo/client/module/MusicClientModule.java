package work.lclpnet.mmo.client.module;

import work.lclpnet.mmo.client.audio.MMOMusic;
import work.lclpnet.mmo.client.event.LeaveWorldCallback;

public class MusicClientModule implements IClientModule {

    @Override
    public void register() {
        LeaveWorldCallback.EVENT.register(world -> {
            MMOMusic.setLoop(false);
            MMOMusic.play(null);
        });
    }
}
