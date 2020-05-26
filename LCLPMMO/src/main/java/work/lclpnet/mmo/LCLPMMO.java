package work.lclpnet.mmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LCLPMMO.MODID)
public class LCLPMMO {
	
	public static final String MODID = "lclpmmo";
	private static final Logger LOGGER = LogManager.getLogger();

	public LCLPMMO() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		IEventBus bus = MinecraftForge.EVENT_BUS;
		bus.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) { //preinit
		LOGGER.info("LCLPMMO initializing...");

		LOGGER.info("LCLPMMO initialized.");
	}

}
