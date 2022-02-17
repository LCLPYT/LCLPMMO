package work.lclpnet.mmo.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;
import work.lclpnet.mmo.sound.MMOSounds;

@Environment(EnvType.CLIENT)
public class MMOMusic {

    public static final MMOMusicSound MAIN_MENU_MUSIC = new MMOMusicSound(MMOSounds.MUSIC_LS5, 20, 600, true);

    private static SoundEvent queuedMusic = null;
    private static SoundEvent lastMusic = null;
    private static boolean loop = false;

    public static boolean shouldLoop() {
        return loop;
    }

    public static void setLoop(boolean loop) {
        MMOMusic.loop = loop;
    }

    public static SoundEvent getLastMusic() {
        return lastMusic;
    }

    public static SoundEvent getQueuedMusic() {
        return queuedMusic;
    }

    public static void play(SoundEvent sound) {
        if (queuedMusic != null && sound == null) lastMusic = queuedMusic;
        else lastMusic = null;

        queuedMusic = sound;
    }
}
