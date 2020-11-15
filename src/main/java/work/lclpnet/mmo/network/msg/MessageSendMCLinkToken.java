package work.lclpnet.mmo.network.msg;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.event.custom.MCLinkTokenReceivedEvent;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

public class MessageSendMCLinkToken implements IMessage {

	private static final Map<Context, ServerLoginNetHandler> handlers = new HashMap<>();

	public static void storeLoginHandler(Context ctx, ServerLoginNetHandler handler) {
		handlers.put(ctx, handler);
	}

	private String token;

	public MessageSendMCLinkToken(String token) {
		this.token = token;
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		if(FMLEnvironment.dist != Dist.DEDICATED_SERVER) return;

		final ServerLoginNetHandler handler = handlers.remove(ctx.get());
		if(handler == null) throw new IllegalStateException("Sender might not be null!");

		MinecraftForge.EVENT_BUS.post(new MCLinkTokenReceivedEvent(handler, this.token));
	}

	public static class Serializer implements IMessageSerializer<MessageSendMCLinkToken> {

		@Override
		public void encode(MessageSendMCLinkToken message, PacketBuffer pb) {
			boolean nonNull = message.token != null;
			pb.writeBoolean(nonNull);
			if(nonNull) pb.writeString(message.token, 36);
		}

		@Override
		public MessageSendMCLinkToken decode(PacketBuffer buffer) {
			boolean nonNull = buffer.readBoolean();
			String token = nonNull ? buffer.readString(36) : null;
			return new MessageSendMCLinkToken(token);
		}

	}

}
