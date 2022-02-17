package work.lclpnet.mmo.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmocontent.asm.mixin.common.SoundEventAccessor;

import java.util.ArrayList;
import java.util.List;

public class MMOSounds {

    private static List<SoundEvent> sounds = new ArrayList<>();

    public static final SoundEvent MUSIC_LS5 = register("music.ls5"),
            MUSIC_TUTORIAL_01 = register("music.tutorial_01"),
            MUSIC_TUTORIAL_02 = register("music.tutorial_02"),
            INTRO_THEME = register("intro_theme"),
            INTRO_THEME_ALT = register("intro_theme_alt"),
            UI_BUTTON_HOVER = register("ui.button.hover"),
            ENTITY_PIXIE_HURT = register("entity.pixie.hurt"),
            ENTITY_PIXIE_DEATH = register("entity.pixie.death"),
            ENTITY_PIXIE_AMBIENT = register("entity.pixie.ambient"),
            ENTITY_PIXIE_SAY = register("entity.pixie.say"),
            ENTITY_BOLETUS_IDLE = register("entity.boletus.idle"),
            ENTITY_BOLETUS_STEP = register("entity.boletus.step"),
            ENTITY_BOLETUS_DEATH = register("entity.boletus.death"),
            ENTITY_BOLETUS_HURT = register("entity.boletus.hurt"),
            ENTITY_BOLETUS_ANGRY = register("entity.boletus.angry"),
            ENTITY_BOLETUS_SPORES = register("entity.boletus.spores");

    public static SoundEvent register(String name) {
        Identifier loc = LCLPMMO.identifier(name);
        SoundEvent event = new SoundEvent(loc);
        sounds.add(event);
        return event;
    }

    public static void init() {
        if (sounds == null) throw new IllegalStateException("Sounds are already initialized");
        sounds.forEach(sound -> Registry.register(Registry.SOUND_EVENT, ((SoundEventAccessor) sound).getId(), sound));
        sounds = null;
    }
}
