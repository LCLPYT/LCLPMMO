package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.event.custom.TutorialStartEvent;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.MMOPacketHandler;

public class MessageTutorial implements IMessage<MessageTutorial> {

	private Type type;
	
	public MessageTutorial() {}
	
	public MessageTutorial(Type type) {
		this.type = type;
	}
	
	@Override
	public void encode(MessageTutorial message, PacketBuffer buffer) {
		buffer.writeEnumValue(message.type);
	}

	@Override
	public MessageTutorial decode(PacketBuffer buffer) {
		Type type = buffer.readEnumValue(Type.class);
		return new MessageTutorial(type);
	}

	@Override
	public void handle(MessageTutorial message, Supplier<Context> supplier) {
		if(FMLEnvironment.dist == Dist.DEDICATED_SERVER) message.handleServer(supplier);
		else if(FMLEnvironment.dist == Dist.CLIENT) message.handleClient(supplier);
	}
	
	@OnlyIn(Dist.DEDICATED_SERVER)
	private void handleServer(Supplier<Context> supplier) {
		switch (this.type) {
		case START_TUTORIAL:
			MinecraftForge.EVENT_BUS.post(new TutorialStartEvent());
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
			MinecraftForge.EVENT_BUS.post(new TutorialStartEvent());
			Minecraft.getInstance().displayGuiScreen(null);
			break;

		default:
			break;
		}
	}
	
	public static enum Type {
		START_TUTORIAL,
		ACKNOCKLEDGE_START
	}

}
