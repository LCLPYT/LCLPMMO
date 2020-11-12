package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.gui.TutorialScreen;
import work.lclpnet.mmo.network.IMessage;

public class MessageShowTutorialScreen implements IMessage<MessageShowTutorialScreen> {

	private boolean skip = false;

	public MessageShowTutorialScreen() {}

	public MessageShowTutorialScreen(boolean skip) {
		this.skip = skip;
	}

	@Override
	public void encode(MessageShowTutorialScreen message, PacketBuffer buffer) {
		buffer.writeBoolean(message.skip);
	}

	@Override
	public MessageShowTutorialScreen decode(PacketBuffer buffer) {
		boolean skip = buffer.readBoolean();
		return new MessageShowTutorialScreen(skip);
	}

	@Override
	public void handle(MessageShowTutorialScreen message, Supplier<Context> supplier) {
		supplier.get().setPacketHandled(true);
		if(FMLEnvironment.dist == Dist.CLIENT) 
			message.showTutorialScreen();
	}

	@OnlyIn(Dist.CLIENT)
	public void showTutorialScreen() {
		if(ClientCache.needCache) ClientCache.cached = this;
		else {
			ClientCache.cached = null;
			Minecraft.getInstance().displayGuiScreen(new TutorialScreen(skip));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static class ClientCache {

		public static MessageShowTutorialScreen cached = null;
		public static boolean needCache = true;

	}

}
