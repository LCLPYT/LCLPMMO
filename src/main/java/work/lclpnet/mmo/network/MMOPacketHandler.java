package work.lclpnet.mmo.network;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.network.msg.MessageDialog;
import work.lclpnet.mmo.network.msg.MessageDisconnectMe;
import work.lclpnet.mmo.network.msg.MessageMMOMusic;
import work.lclpnet.mmo.network.msg.MessageMusic;
import work.lclpnet.mmo.network.msg.MessageRequestMCLink;
import work.lclpnet.mmo.network.msg.MessageSendMCLinkToken;
import work.lclpnet.mmo.network.msg.MessageShowTutorialScreen;
import work.lclpnet.mmo.network.msg.MessageTutorial;

@EventBusSubscriber(bus = Bus.FORGE, modid = LCLPMMO.MODID)
public class MMOPacketHandler {

	private static final String PROTOCOL_VERSION = "3";
	public static final ResourceLocation RESOURCE_LOCATION = new ResourceLocation(LCLPMMO.MODID, "main");
	public static SimpleChannel INSTANCE = null;
	private static int nextId = 0;

	private static Map<Integer, Pair<Class<?>, IMessageSerializer<?>>> recordedMSG = new HashMap<>();

	public static void init() {
		INSTANCE = NetworkRegistry.newSimpleChannel(
				RESOURCE_LOCATION, 
				() -> PROTOCOL_VERSION, 
				PROTOCOL_VERSION::equals, 
				PROTOCOL_VERSION::equals
				);

		register(MessageMusic.class, new MessageMusic.Serializer());
		register(MessageSendMCLinkToken.class, new MessageSendMCLinkToken.Serializer(), true);
		register(MessageRequestMCLink.class, new MessageRequestMCLink.Serializer());
		register(MessageDisconnectMe.class, new MessageDisconnectMe.Serializer());
		register(MessageShowTutorialScreen.class, new MessageShowTutorialScreen.Serializer());
		register(MessageTutorial.class, new MessageTutorial.Serializer());
		register(MessageMMOMusic.class, new MessageMMOMusic.Serializer());
		register(MessageDialog.class, new MessageDialog.Serializer());
	}

	private static <T extends IMessage> void register(Class<T> clazz, IMessageSerializer<T> msg) {
		register(clazz, msg, false);
	}

	private static <T extends IMessage> void register(Class<T> clazz, IMessageSerializer<T> msg, boolean record) {
		int id = nextId++;
		INSTANCE.registerMessage(
				id, 
				clazz, 
				msg::encode, 
				msg::decode, 
				msg::handle);
		if(record) recordedMSG.put(id, Pair.of(clazz, msg));
	}

	public static Pair<Class<?>, IMessageSerializer<?>> getRecordedMsg(PacketBuffer pb) {
		int id = pb.readByte();
		Pair<Class<?>, IMessageSerializer<?>> msg = recordedMSG.get(id);
		return msg;
	}
	
	public static void sendToClient(ServerPlayerEntity player, IMessage msg) {
		MMOPacketHandler.INSTANCE.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void sendToServer(IMessage msg) {
		MMOPacketHandler.INSTANCE.sendToServer(msg);
	}
	
}
