package work.lclpnet.mmo.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvent;

@Environment(EnvType.CLIENT)
public class MMOMusicSound extends MusicSound {

    public MMOMusicSound(SoundEvent sound, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
        super(sound, minDelay, maxDelay, replaceCurrentMusic);
    }
}
