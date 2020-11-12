package work.lclpnet.mmo.network;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public interface IMessageSerializer<T> {

	void encode(T message, PacketBuffer buffer);
	
	T decode(PacketBuffer buffer);
	
	void handle(T message, Supplier<NetworkEvent.Context> supplier);
	
}
