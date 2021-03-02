package work.lclpnet.mmo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.OptionsSoundsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.asm.type.IMMOEntity;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.event.custom.EntityRightClickedEvent;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.gui.login.ResponsiveCheckboxButton;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.network.msg.MessageShowTutorialScreen;
import work.lclpnet.mmo.util.RenderWorker;
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
		if(e.phase == Phase.END) RenderWorker.workRender();
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onGuiClose(GuiOpenEvent e) {
		if(e.getGui() != null) return;

		MessageShowTutorialScreen.ClientCache.needCache = false;
		if(MessageShowTutorialScreen.ClientCache.cached != null) 
			RenderWorker.enqueueOnRender(MessageShowTutorialScreen.ClientCache.cached::showTutorialScreen);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onWorldLeave(WorldEvent.Unload e) {
		MessageShowTutorialScreen.ClientCache.needCache = true;
		MusicSystem.stopAllSound(x -> {});
		MusicSystem.setLoopBackgroundMusic(false);
		MusicSystem.playBackgroundMusic(null);
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

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract e) {
		if(!(e.getTarget().world instanceof ServerWorld) || e.getHand() != Hand.MAIN_HAND) return;
		
		EntityRightClickedEvent event = new EntityRightClickedEvent(e.getPlayer(), e.getTarget(), false);
		MinecraftForge.EVENT_BUS.post(event);
		if(event.isCanceled()) e.setCanceled(true);
	}
	
	@SubscribeEvent
	public static void onEntityRightClicked(EntityRightClickedEvent e) {
		boolean shouldCancel = IMMOEntity.get(e.getClicked()).onClick(e.getPlayer());
		if(shouldCancel) e.setCanceled(true);
	}
	
	/*@SubscribeEvent
	public static void onServerChat(ServerChatEvent e) {
		ServerPlayerEntity p = e.getPlayer();
		DialogData data = new DialogData(Arrays.asList(
				new DialogFragment("§aLorem ipsum dolor sit amet§7, \n\nconsetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet."),
				new DialogFragment("AHHHHHHHHHHHHHHHHHJAJAJA JAJAJA JA YA YAAAA"),
				new DialogFragment("yayayayayaaaa")));
		IMMOPlayer.get(p).openMMODialog(new Dialog(p, data).setDismissable(true));
	}*/

}
