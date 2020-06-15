package work.lclpnet.mmo.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.network.msg.MessageMusic;

public class MMOPacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel INSTANCE = null;
	private static int nextId = 0;
	
	public static void init() {
		INSTANCE = NetworkRegistry.newSimpleChannel(
				new ResourceLocation(LCLPMMO.MODID, "main"), 
				() -> PROTOCOL_VERSION, 
				PROTOCOL_VERSION::equals, 
				PROTOCOL_VERSION::equals
				);
		register(MessageMusic.class, new MessageMusic());
	}
	
	private static <T> void register(Class<T> clazz, IMessage<T> msg) {
		INSTANCE.registerMessage(
				nextId++, 
				clazz, 
				msg::encode, 
				msg::decode, 
				msg::handle);
	}
	
}
