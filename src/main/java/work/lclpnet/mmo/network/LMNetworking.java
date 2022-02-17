package work.lclpnet.mmo.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import work.lclpnet.mmo.network.packet.MMOEntityAnimationPacket;
import work.lclpnet.mmocontent.networking.MMOPacketRegistrar;
import work.lclpnet.mmocontent.util.Env;

public class LMNetworking {

    private static MMOPacketRegistrar registrar = null;

    public static void registerPackets() {
        if (registrar != null) return; // already registered

        registrar = new MMOPacketRegistrar(LogManager.getLogger());
        registrar.register(MMOEntityAnimationPacket.ID, new MMOEntityAnimationPacket.Decoder());

        // register serializers
        MMODataSerializers.registerSerializers();
    }

    @Environment(EnvType.CLIENT)
    public static void registerClientPacketHandlers() {
        registrar.registerClientPacketHandlers();
        registrar = null; // should be called last on client
    }

    public static void registerServerPacketHandlers() {
        registrar.registerServerPacketHandlers();
        if (!Env.isClient()) registrar = null; // not needed any further on a dedicated server
    }
}
