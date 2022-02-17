package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MusicTracker.class)
public interface MusicTrackerAccessor {

    @Accessor
    void setTimeUntilNextSong(int ticks);
}
