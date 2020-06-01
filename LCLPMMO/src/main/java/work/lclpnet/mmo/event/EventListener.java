package work.lclpnet.mmo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.gui.MMOMainScreen;
import work.lclpnet.mmo.gui.PreIntroScreen;

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
			Minecraft.getInstance().displayGuiScreen(Config.shouldSkipIntro() || !startup ? new MMOMainScreen(true) : new PreIntroScreen());
			startup = false;
		}
	}
	
}
