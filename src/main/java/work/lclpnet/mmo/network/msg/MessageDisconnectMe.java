package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessageSerializer;

public class MessageDisconnectMe {

	private ITextComponent msg;
	
	public MessageDisconnectMe() {}
	
	public MessageDisconnectMe(ITextComponent message) {
		this.msg = message;
	}
	
	public static class Serializer implements IMessageSerializer<MessageDisconnectMe> {
		
		@Override
		public void encode(MessageDisconnectMe message, PacketBuffer buffer) {
			buffer.writeTextComponent(message.msg);
		}

		@Override
		public MessageDisconnectMe decode(PacketBuffer buffer) {
			ITextComponent msg = buffer.readTextComponent();
			return new MessageDisconnectMe(msg);
		}

		@Override
		public void handle(MessageDisconnectMe message, Supplier<Context> supplier) {
			supplier.get().setPacketHandled(true);
			if(FMLEnvironment.dist != Dist.DEDICATED_SERVER) return;
			
			ServerPlayerEntity sender = supplier.get().getSender();
			supplier.get().enqueueWork(() -> {
				sender.connection.disconnect(message.msg);
			});
		}
		
	}

}