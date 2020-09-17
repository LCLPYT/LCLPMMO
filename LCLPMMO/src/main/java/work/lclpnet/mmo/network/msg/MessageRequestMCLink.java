package work.lclpnet.mmo.network.msg;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.util.LCLPNetwork;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageRequestMCLink implements IMessage<MessageRequestMCLink>{

	public MessageRequestMCLink() {}

	@Override
	public void encode(MessageRequestMCLink message, PacketBuffer buffer) {}

	@Override
	public MessageRequestMCLink decode(PacketBuffer buffer) {
		return new MessageRequestMCLink();
	}

	@Override
	public void handle(MessageRequestMCLink message, Supplier<Context> supplier) {
		if(FMLEnvironment.dist != Dist.CLIENT) return;

		LCLPNetwork.sendRequest("api/auth/request-mclink-token", "POST", null, response -> {
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

			MMOPacketHandler.INSTANCE.sendToServer(new MessageSendMCLinkToken(token));
		});
	}
	
}
