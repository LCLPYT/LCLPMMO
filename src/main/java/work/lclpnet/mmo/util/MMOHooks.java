package work.lclpnet.mmo.util;

import work.lclpnet.mmo.audio.MusicSystem;

public class MMOHooks {

    // called from coremod
    public static void onVolumeChange() {
        MusicSystem.setOverallVolume(MusicSystem.getVolume(), x -> {
        });
    }
}
