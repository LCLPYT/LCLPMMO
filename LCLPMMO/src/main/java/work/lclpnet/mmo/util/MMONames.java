package work.lclpnet.mmo.util;

import work.lclpnet.mmo.LCLPMMO;

public class MMONames {

	private static String loc(String location) {
		return String.format("%s:%s", LCLPMMO.MODID, location);
	}
	
	public static class Block {
		
		public static final String GLASS_BOTTLE = loc("glass_bottle");
		
	}
	
	public static class TileEntity {
		
		public static final String GLASS_BOTTLE = loc("glass_bottle");
		
	}
	
	public static class Sound {
		
		public static final String MUSIC_LS5 = loc("music.ls5"),
				MUSIC_TUTORIAL_01 = loc("music.tutorial_01"),
				MUSIC_TUTORIAL_02 = loc("music.tutorial_02"),
				INTRO_THEME = loc("intro_theme"),
				INTRO_THEME_ALT = loc("intro_theme_alt"),
				UI_BUTTON_HOVER = loc("ui.button.hover");
		
	}
	
}
