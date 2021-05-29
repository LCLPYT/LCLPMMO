package work.lclpnet.mmo.audio;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.util.MMONames.Sound;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.MOD)
public class MMOSoundEvents {

    private static final List<SoundEvent> SOUNDS = new ArrayList<>();

    public static final SoundEvent MUSIC_LS5 = register(Sound.MUSIC_LS5),
            MUSIC_TUTORIAL_01 = register(Sound.MUSIC_TUTORIAL_01),
            MUSIC_TUTORIAL_02 = register(Sound.MUSIC_TUTORIAL_02),
            INTRO_THEME = register(Sound.INTRO_THEME),
            INTRO_THEME_ALT = register(Sound.INTRO_THEME_ALT),
            UI_BUTTON_HOVER = register(Sound.UI_BUTTON_HOVER),
            ENTITY_PIXIE_HURT = register(Sound.ENTITY_PIXIE_HURT),
            ENTITY_PIXIE_DEATH = register(Sound.ENTITY_PIXIE_DEATH),
            ENTITY_PIXIE_AMBIENT = register(Sound.ENTITY_PIXIE_AMBIENT),
            ENTITY_PIXIE_SAY = register(Sound.ENTITY_PIXIE_SAY),
            ENTITY_BOLETUS_IDLE = register(Sound.ENTITY_BOLETUS_IDLE),
            ENTITY_BOLETUS_STEP = register(Sound.ENTITY_BOLETUS_STEP),
            ENTITY_BOLETUS_DEATH = register(Sound.ENTITY_BOLETUS_DEATH),
            ENTITY_BOLETUS_HURT = register(Sound.ENTITY_BOLETUS_HURT),
            ENTITY_BOLETUS_ANGRY = register(Sound.ENTITY_BOLETUS_ANGRY),
            ENTITY_BOLETUS_SPORES = register(Sound.ENTITY_BOLETUS_SPORES);

    private static SoundEvent register(String name) {
        SoundEvent event = new SoundEvent(new ResourceLocation(name));
        event.setRegistryName(name);
        SOUNDS.add(event);
        return event;
    }

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
        SOUNDS.forEach(soundEvent -> event.getRegistry().register(soundEvent));
        SOUNDS.clear();
    }
}
