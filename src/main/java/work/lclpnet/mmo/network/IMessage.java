package work.lclpnet.mmo.network;

import java.util.function.Supplier;

import net.minecraftforge.fml.network.NetworkEvent.Context;

public interface IMessage {

	void handle(Supplier<Context> ctx);
	
	default void wrapHandle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		handle(ctx);
	}
	
}
