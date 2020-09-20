package work.lclpnet.mmo.network;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.network.msg.MessageMusic;
import work.lclpnet.mmo.network.msg.MessageRequestMCLink;
import work.lclpnet.mmo.network.msg.MessageSendMCLinkToken;

@EventBusSubscriber(bus = Bus.FORGE, modid = LCLPMMO.MODID)
public class MMOPacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(LCLPMMO.MODID, "main");
	public static SimpleChannel INSTANCE = null;
	private static int nextId = 0;

	private static Map<Integer, Pair<Class<?>, IMessage<?>>> recordedMSG = new HashMap<>();

	public static void init() {
		INSTANCE = NetworkRegistry.newSimpleChannel(
				RESOURCE_LOCATION, 
				() -> PROTOCOL_VERSION, 
				PROTOCOL_VERSION::equals, 
				PROTOCOL_VERSION::equals
				);

		register(MessageMusic.class, new MessageMusic());
		register(MessageSendMCLinkToken.class, new MessageSendMCLinkToken(), true);
		register(MessageRequestMCLink.class, new MessageRequestMCLink());
	}

	private static <T> void register(Class<T> clazz, IMessage<T> msg) {
		register(clazz, msg, false);
	}

	private static <T> void register(Class<T> clazz, IMessage<T> msg, boolean record) {
		int id = nextId++;
		INSTANCE.registerMessage(
				id, 
				clazz, 
				msg::encode, 
				msg::decode, 
				msg::handle);
		if(record) recordedMSG.put(id, Pair.of(clazz, msg));
	}

	public static Pair<Class<?>, IMessage<?>> getRecordedMsg(PacketBuffer pb) {
		int id = pb.readByte();
		Pair<Class<?>, IMessage<?>> msg = recordedMSG.get(id);
		return msg;
	}
	
}
