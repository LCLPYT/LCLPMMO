package work.lclpnet.mmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;
import work.lclpnet.corebase.util.ComponentSupplier;
import work.lclpnet.mmo.cmd.MMOCommands;
import work.lclpnet.mmo.entity.MMOEntities;
import work.lclpnet.mmo.event.AttributeListener;
import work.lclpnet.mmo.event.EventListener;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.render.ClientRenderHandler;
import work.lclpnet.mmo.util.ColorHandler;
import work.lclpnet.mmo.util.Discord;
import work.lclpnet.mmo.util.EnvironmentUtils;
import work.lclpnet.mmo.util.MMOGroup;
import work.lclpnet.mmo.util.RenderLayerHandler;
import work.lclpnet.mmo.util.network.LCLPNetwork;

@Mod(LCLPMMO.MODID)
public class LCLPMMO {
	
	public static final String MODID = "lclpmmo";
	private static final Logger LOGGER = LogManager.getLogger();
	public static ComponentSupplier TEXT = new ComponentSupplier("LCLPMMO");
	public static MMOGroup GROUP = new MMOGroup(MODID);

	static {
		GeckoLibMod.DISABLE_IN_DEV = true;
	}
	
	public LCLPMMO() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setup);
		modBus.addListener(this::clientSetup);
		modBus.addListener(this::onIMCEnqueue);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(this);
		forgeBus.register(new EventListener());
		forgeBus.register(new AttributeListener());
		
		GeckoLib.initialize();
	}

	private void setup(final FMLCommonSetupEvent event) { //preinit
		LOGGER.info("LCLPMMO initializing...");

		Config.load();
		LCLPNetwork.setup(() -> {});
		MMOPacketHandler.init();
		MMOCommands.registerArgumentTypes();
		MMOEntities.registerEntityTypeAttributes();
		
		if(FMLEnvironment.dist == Dist.CLIENT) EnvironmentUtils.deleteTmpDir();
		
		LOGGER.info("LCLPMMO initialized.");
	}
	
	@SubscribeEvent
	public void serverStart(final FMLServerStartingEvent e) {
		LOGGER.info("LCLPMMO server starting...");
	}
	
	@SubscribeEvent
	public void onRegisterCommands(RegisterCommandsEvent e) {
		MMOCommands.registerCommands(e.getDispatcher(), e.getEnvironment());
	}
	
	private void clientSetup(final FMLClientSetupEvent e) {
		LOGGER.info("LCLPMMO client starting...");
		
		RenderLayerHandler.init();
		ColorHandler.init();
		ClientRenderHandler.setup();
		
		boolean FORCE_DISABLE = true; // TODO remove in release
		if(!FORCE_DISABLE && Config.enableDiscordIntegration()) Discord.initRPC();
	}
	
	public void onIMCEnqueue(InterModEnqueueEvent e) {
		InterModComms.sendTo(LCLPMMO.MODID, "lclpupdater", "defineStartingScreens", () -> new Class<?>[] {
			MMOMainScreen.class,
			PreIntroScreen.class,
			LoginScreen.class
		});
	}

}
