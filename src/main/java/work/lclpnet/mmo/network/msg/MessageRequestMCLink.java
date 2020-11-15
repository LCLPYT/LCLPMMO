package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.util.network.LCLPNetwork;

public class MessageRequestMCLink implements IMessage {

	@Override
	public void handle(Supplier<Context> ctx) {
		if(FMLEnvironment.dist != Dist.CLIENT) return;

		LCLPNetwork.post("api/auth/request-mclink-token", null, response -> {
			String token;
			try {
				if (response.isNoConnection() || response.getResponseCode() != 201) throw new IllegalStateException();

				token = new Gson()
						.fromJson(response.getRawResponse(), JsonObject.class)
						.get("token")
						.getAsString();
			} catch (Exception e) {
				token = null;
			}

			MMOPacketHandler.INSTANCE.reply(new MessageSendMCLinkToken(token), ctx.get());
		});
	}

	public static class Serializer implements IMessageSerializer<MessageRequestMCLink> {

		@Override
		public void encode(MessageRequestMCLink message, PacketBuffer buffer) {}

		@Override
		public MessageRequestMCLink decode(PacketBuffer buffer) {
			return new MessageRequestMCLink();
		}

	}

}
