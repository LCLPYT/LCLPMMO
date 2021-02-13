package work.lclpnet.mmo.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface IMessageSerializer<T extends IMessage> {

	void encode(T message, PacketBuffer buffer);
	
	T decode(PacketBuffer buffer);
	
	default void handle(T message, Supplier<NetworkEvent.Context> supplier) {
		message.wrapHandle(supplier);
	}
	
}
