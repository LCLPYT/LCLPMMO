package work.lclpnet.mmo;

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
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;
import work.lclpnet.corebase.CoreBase;
import work.lclpnet.corebase.util.ComponentSupplier;
import work.lclpnet.mmo.client.ColorHandler;
import work.lclpnet.mmo.client.Discord;
import work.lclpnet.mmo.client.RenderLayerHandler;
import work.lclpnet.mmo.client.input.MMOKeybindings;
import work.lclpnet.mmo.client.render.ClientRenderHandler;
import work.lclpnet.mmo.cmd.MMOCommands;
import work.lclpnet.mmo.gui.PreIntroScreen;
import work.lclpnet.mmo.gui.login.LoginScreen;
import work.lclpnet.mmo.gui.main.MMOMainScreen;
import work.lclpnet.mmo.item.MMOItems;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.util.MMOUtils;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import javax.annotation.Nullable;

@Mod(LCLPMMO.MODID)
public class LCLPMMO {

    public static final String MODID = "lclpmmo";
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentSupplier TEXT = new ComponentSupplier("LCLPMMO");
    public static final MMOItems.MMOItemGroup GROUP = new MMOItems.MMOItemGroup(MODID);
    private static boolean serverStarted = false, shutdown = false;
    private static String shutdownReason = null;

    static {
        if (!FMLEnvironment.production) GeckoLibMod.DISABLE_IN_DEV = true;
    }

    public LCLPMMO() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::setup);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::onIMCEnqueue);

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(this);

        if (FMLEnvironment.dist == Dist.CLIENT) GeckoLib.initialize();
    }

    private void setup(final FMLCommonSetupEvent event) { //preinit
        LOGGER.info("LCLPMMO initializing...");

        Config.load();
        LCLPNetwork.setup(() -> {
        });
        MMOPacketHandler.init();
        MMOCommands.registerArgumentTypes();

        if (FMLEnvironment.dist == Dist.CLIENT) MMOUtils.deleteTmpDir();

        LOGGER.info("LCLPMMO initialized.");
    }

    @SubscribeEvent
    public void serverStart(final FMLServerStartingEvent e) {
        LOGGER.info("LCLPMMO server starting...");
    }

    @SubscribeEvent
    public void serverStarted(final FMLServerStartedEvent e) {
        serverStarted = true;
        if (shutdown) doShutdown();
    }

    public static void shutdownServer(@Nullable String reason) {
        shutdownReason = reason;
        if (serverStarted) doShutdown();
        else shutdown = true;
    }

    private static void doShutdown() {
        LOGGER.info("Shutting down server: {}", shutdownReason != null ? shutdownReason : "No reason provided.");
        CoreBase.getServer().initiateShutdown(false);
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
        MMOKeybindings.init();

        boolean FORCE_DISABLE = true; // TODO remove in release
        if (!FORCE_DISABLE && Config.enableDiscordIntegration()) Discord.initRPC();
    }

    public void onIMCEnqueue(InterModEnqueueEvent e) {
        InterModComms.sendTo(LCLPMMO.MODID, "lclpupdater", "defineStartingScreens", () -> new Class<?>[]{
                MMOMainScreen.class,
                PreIntroScreen.class,
                LoginScreen.class
        });
    }
}
