package work.lclpnet.mmo.network.msg;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessage;

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
		//ServerPlayerEntity spe = supplier.get().getSender();
		// TODO
	}
	
	public UUID getToken() {
		return token;
	}

}
