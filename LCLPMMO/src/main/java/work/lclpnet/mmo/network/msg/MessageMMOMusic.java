package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.network.IMessage;

public class MessageMMOMusic implements IMessage<MessageMMOMusic> {

	private SoundEvent music;
	
	public MessageMMOMusic() {}
	
	public MessageMMOMusic(SoundEvent music) {
		this.music = music;
	}
	
	@Override
	public void encode(MessageMMOMusic message, PacketBuffer buffer) {
		buffer.writeResourceLocation(message.music.getRegistryName());
	}

	@Override
	public MessageMMOMusic decode(PacketBuffer buffer) {
		SoundEvent music = new SoundEvent(buffer.readResourceLocation());
		return new MessageMMOMusic(music);
	}

	@Override
	public void handle(MessageMMOMusic message, Supplier<Context> supplier) {
		supplier.get().setPacketHandled(true);
		if(FMLEnvironment.dist != Dist.CLIENT) return;
		
		message.playMusic();
	}
	
	@OnlyIn(Dist.CLIENT)
	private void playMusic() {
		if(music != null) MusicSystem.playBackgroundMusic(music);
	}

}
