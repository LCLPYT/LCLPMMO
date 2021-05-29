package work.lclpnet.mmo.audio;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.util.SoundEvent;

/**
 * Marker class for non vanilla music
 */
public class MMOBackgroundMusicSelector extends BackgroundMusicSelector {

    public MMOBackgroundMusicSelector(SoundEvent soundEvent, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        super(soundEvent, minDelay, maxDelay, replaceCurrentMusic);
    }
}
