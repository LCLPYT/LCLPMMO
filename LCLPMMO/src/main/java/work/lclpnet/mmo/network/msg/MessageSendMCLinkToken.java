package work.lclpnet.mmo.network.msg;

import java.util.UUID;
import java.util.function.Supplier;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.util.LCLPNetwork;

public class MessageSendMCLinkToken implements IMessage<MessageSendMCLinkToken>{

	private UUID token;
	
	public MessageSendMCLinkToken() {}
	
	public MessageSendMCLinkToken(UUID token) {
		this.token = token;
	}
	
	@Override
	public void encode(MessageSendMCLinkToken message, PacketBuffer buffer) {
		buffer.writeUniqueId(message.token);
	}

	@Override
	public MessageSendMCLinkToken decode(PacketBuffer buffer) {
		return new MessageSendMCLinkToken(buffer.readUniqueId());
	}

	@Override
	public void handle(MessageSendMCLinkToken message, Supplier<Context> supplier) {
		ServerPlayerEntity spe = supplier.get().getSender();
		if(spe == null) throw new IllegalStateException("Sender might not be null!");

		JsonObject body = new JsonObject();
		body.addProperty("mcUuid", spe.getUniqueID().toString());
		body.addProperty("token", message.getToken().toString());

		LCLPNetwork.sendRequest("api/auth/process-mclink-token", "POST", body, response -> {
			if(response.isNoConnection()) {
				System.out.println("No connection.");
			}
			System.out.println("RESPONSE: " + response.getResponseCode());
			System.out.println(response.getRawResponse());
		});
	}
	
	public UUID getToken() {
		return token;
	}

}
