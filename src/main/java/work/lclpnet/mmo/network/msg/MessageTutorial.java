package work.lclpnet.mmo.network.msg;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.event.custom.TutorialStartEvent;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;
import work.lclpnet.mmo.network.MMOPacketHandler;

import java.util.function.Supplier;

public class MessageTutorial implements IMessage {

	private Type type;
	
	public MessageTutorial(Type type) {
		this.type = type;
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private void handleServer(Supplier<Context> supplier) {
		switch (this.type) {
		case START_TUTORIAL:
			MinecraftForge.EVENT_BUS.post(new TutorialStartEvent(supplier.get().getSender()));
			MMOPacketHandler.INSTANCE.reply(new MessageTutorial(Type.ACKNOCKLEDGE_START), supplier.get());
			break;

		default:
			break;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private void handleClient(Supplier<Context> supplier) {
		switch (this.type) {
		case ACKNOCKLEDGE_START:
			MinecraftForge.EVENT_BUS.post(new TutorialStartEvent(null));
			Minecraft.getInstance().displayGuiScreen(null);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void handle(Supplier<Context> ctx) {
		if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) handleServer(ctx);
		else if(FMLEnvironment.dist == Dist.CLIENT) handleClient(ctx);		
	}
	
	public static enum Type {
		START_TUTORIAL,
		ACKNOCKLEDGE_START
	}
	
	public static class Serializer implements IMessageSerializer<MessageTutorial> {
		
		@Override
		public void encode(MessageTutorial message, PacketBuffer buffer) {
			buffer.writeEnumValue(message.type);
		}

		@Override
		public MessageTutorial decode(PacketBuffer buffer) {
			Type type = buffer.readEnumValue(Type.class);
			return new MessageTutorial(type);
		}

	}

}
