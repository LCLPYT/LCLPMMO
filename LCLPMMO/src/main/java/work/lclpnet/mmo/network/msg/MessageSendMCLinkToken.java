package work.lclpnet.mmo.network.msg;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.event.custom.MCLinkTokenReceivedEvent;
import work.lclpnet.mmo.network.IMessage;

public class MessageSendMCLinkToken implements IMessage<MessageSendMCLinkToken>{

	private String token;
	
	public MessageSendMCLinkToken() {}
	
	public MessageSendMCLinkToken(String token) {
		this.token = token;
	}
	
	@Override
	public void encode(MessageSendMCLinkToken message, PacketBuffer buffer) {
		boolean nonNull = message.token != null;
		buffer.writeBoolean(nonNull);
		if(nonNull) buffer.writeByteArray(message.token.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public MessageSendMCLinkToken decode(PacketBuffer buffer) {
		boolean nonNull = buffer.readBoolean();
		String token = null;
		if(nonNull) token = new String(buffer.readByteArray(), StandardCharsets.UTF_8);

		return new MessageSendMCLinkToken(token);
	}

	@Override
	public void handle(MessageSendMCLinkToken message, Supplier<Context> supplier) {
		if(FMLEnvironment.dist != Dist.DEDICATED_SERVER) return;

		final ServerPlayerEntity spe = supplier.get().getSender();
		if(spe == null) throw new IllegalStateException("Sender might not be null!");

		MinecraftForge.EVENT_BUS.post(new MCLinkTokenReceivedEvent(spe, message.getToken()));

		/*JsonObject body = new JsonObject();
		body.addProperty("mcUuid", spe.getUniqueID().toString());
		body.addProperty("token", message.getToken().toString());

		LCLPNetwork.sendRequest("", "POST", body, response -> {});*/
	}
	
	public String getToken() {
		return token;
	}

}
