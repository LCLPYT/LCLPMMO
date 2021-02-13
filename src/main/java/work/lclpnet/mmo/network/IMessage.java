package work.lclpnet.mmo.network;

import net.minecraftforge.fml.network.NetworkEvent.Context;

import java.util.function.Supplier;

public interface IMessage {

	void handle(Supplier<Context> ctx);
	
	default void wrapHandle(Supplier<Context> ctx) {
		ctx.get().setPacketHandled(true);
		handle(ctx);
	}
	
}
