package work.lclpnet.mmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import work.lclpnet.mmo.event.EventListener;
import work.lclpnet.mmo.util.RenderLayerHandler;

@Mod(LCLPMMO.MODID)
public class LCLPMMO {
	
	public static final String MODID = "lclpmmo";
	private static final Logger LOGGER = LogManager.getLogger();
	public static MMOGroup GROUP = new MMOGroup(MODID);

	public LCLPMMO() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(this::setup);
		modBus.addListener(this::clientSetup);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.register(this);
		forgeBus.register(new EventListener());
	}

	private void setup(final FMLCommonSetupEvent event) { //preinit
		LOGGER.info("LCLPMMO initializing...");

		Config.load();
		
		LOGGER.info("LCLPMMO initialized.");
	}
	
	private void clientSetup(final FMLClientSetupEvent e) {
		RenderLayerHandler.init();
	}

}
