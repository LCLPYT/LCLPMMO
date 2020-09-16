package work.lclpnet.mmo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.util.LCLPNetwork;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.FORGE)
public class EventListener {

	private static boolean startup = true;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onSound(PlaySoundEvent e) {
		if(e.getSound().getCategory() == SoundCategory.MUSIC 
				&& Minecraft.getInstance().world == null 
				&& e.getSound().getSoundLocation().getNamespace().equals("minecraft")) 
			e.setResultSound(null);
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onGui(GuiOpenEvent e) {
		if(e.getGui() instanceof MainMenuScreen) {
			e.setCanceled(true);
			if(LCLPNetwork.getAccessToken() == null) Minecraft.getInstance().displayGuiScreen(new LoginScreen());
			else Minecraft.getInstance().displayGuiScreen(getStartingScreen());
		}
	}

	public static Screen getStartingScreen() {
		Screen screen = Config.shouldSkipIntro() || !startup ? new MMOMainScreen(true) : new PreIntroScreen();
		startup = false;
		return screen;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onWorldLeave(WorldEvent.Unload e) {
		MusicSystem.stopAllSound(x -> {});
	}
	
}
