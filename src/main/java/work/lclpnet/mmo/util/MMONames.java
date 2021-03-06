package work.lclpnet.mmo.util;

import work.lclpnet.mmo.LCLPMMO;

public class MMONames {

    private static String loc(String location) {
        return String.format("%s:%s", LCLPMMO.MODID, location);
    }

    public static class Item {

        public static final String PIXIE_SPAWN_EGG = loc("pixie_spawn_egg"),
                BOLETUS_SPAWN_EGG = "boletus_spawn_egg";
    }

    public static class Block {

        public static final String GLASS_BOTTLE = loc("glass_bottle");
    }

    public static class TileEntity {

        public static final String GLASS_BOTTLE = loc("glass_bottle");
    }

    public static class Entity {

        public static final String PIXIE = loc("pixie"),
                BOLETUS = loc("boletus"),
                FALLEN_KNIGHT = loc("fallen_knight");
    }

    public static class Sound {

        public static final String MUSIC_LS5 = loc("music.ls5"),
                MUSIC_TUTORIAL_01 = loc("music.tutorial_01"),
                MUSIC_TUTORIAL_02 = loc("music.tutorial_02"),
                INTRO_THEME = loc("intro_theme"),
                INTRO_THEME_ALT = loc("intro_theme_alt"),
                UI_BUTTON_HOVER = loc("ui.button.hover"),
                ENTITY_PIXIE_HURT = loc("entity.pixie.hurt"),
                ENTITY_PIXIE_AMBIENT = loc("entity.pixie.ambient"),
                ENTITY_PIXIE_DEATH = loc("entity.pixie.death"),
                ENTITY_PIXIE_SAY = loc("entity.pixie.say"),
                ENTITY_BOLETUS_IDLE = loc("entity.boletus.idle"),
                ENTITY_BOLETUS_STEP = loc("entity.boletus.step"),
                ENTITY_BOLETUS_DEATH = loc("entity.boletus.death"),
                ENTITY_BOLETUS_HURT = loc("entity.boletus.hurt"),
                ENTITY_BOLETUS_ANGRY = loc("entity.boletus.angry"),
                ENTITY_BOLETUS_SPORES = loc("entity.boletus.spores");
    }

    public static class Particle {

        public static final String SPORES = loc("spores");
    }

    public static class DataSerializer {

        public static final String VECTOR_3D = loc("vec3d");
    }
}
