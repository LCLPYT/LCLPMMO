package work.lclpnet.mmo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.gui.login.ResponsiveCheckboxButton;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.network.msg.MessageShowTutorialScreen;
import work.lclpnet.mmo.util.Enqueuer;
import work.lclpnet.mmo.util.network.LCLPNetwork;

@EventBusSubscriber(modid = LCLPMMO.MODID, bus = Bus.FORGE)
public class EventListener {

	private static boolean startup = true;

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onGui(GuiOpenEvent e) {
		if(e.getGui() instanceof MainMenuScreen) {
			e.setCanceled(true);
			if(LCLPNetwork.getAccessToken() == null) Minecraft.getInstance().displayGuiScreen(new LoginScreen());
			else Minecraft.getInstance().displayGuiScreen(getStartingScreen());
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static Screen getStartingScreen() {
		Screen screen = Config.shouldSkipIntro() || !startup ? new MMOMainScreen(true) : new PreIntroScreen();
		startup = false;
		return screen;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent e) {
		if(e.phase == Phase.END) Enqueuer.workRender();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onGuiClose(GuiOpenEvent e) {
		if(e.getGui() != null) return;
		
		MessageShowTutorialScreen.ClientCache.needCache = false;
		if(MessageShowTutorialScreen.ClientCache.cached != null) 
			Enqueuer.enqueueOnRender(MessageShowTutorialScreen.ClientCache.cached::showTutorialScreen);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onWorldLeave(WorldEvent.Unload e) {
		MessageShowTutorialScreen.ClientCache.needCache = true;
		MusicSystem.stopAllSound(x -> {});
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onSoundSettingsScreen(InitGuiEvent e) {
        Screen gui = e.getGui();
        if(!(gui instanceof OptionsSoundsScreen)) return;
        
		ResponsiveCheckboxButton checkbox = new ResponsiveCheckboxButton(10, 10, 150, 20, new TranslationTextComponent("options_screen.audio.only_mmo"), Config.isMinecraftMusicDisabled());
        checkbox.setResponder(Config::setMinecraftMusicDisabled);
		e.addWidget(checkbox);
	}
	
	/*@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onEntityInteract(EntityInteract e) {
		Entity en = e.getTarget();
		if(!(en instanceof LivingEntity)) return;
		LivingEntity le = (LivingEntity) en;
		Minecraft.getInstance().displayGuiScreen(new DialogScreen<>(true, le));
	}*/
	
}
