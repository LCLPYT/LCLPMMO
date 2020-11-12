package work.lclpnet.mmo.network.msg;

import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.network.IMessageSerializer;

public class MessageMMOMusic {

	private boolean stop = false;
	private boolean loop = false;
	private SoundEvent music;
	
	public MessageMMOMusic() {}
	
	public MessageMMOMusic(SoundEvent music) {
		this(music, false, false);
	}
	
	public MessageMMOMusic(SoundEvent music, boolean stop, boolean loop) {
		this.music = music;
		this.stop = stop;
		this.loop = loop;
	}
	
	@OnlyIn(Dist.CLIENT)
	private void setLoop() {
		MusicSystem.setLoopBackgroundMusic(loop);
	}

	@OnlyIn(Dist.CLIENT)
	private void stopMusic() {
		MusicSystem.setLoopBackgroundMusic(false);
		MusicSystem.playBackgroundMusic(null);
	}

	@OnlyIn(Dist.CLIENT)
	private void playMusic() {
		if(music != null) MusicSystem.playBackgroundMusic(music);
	}
	
	public static class Serializer implements IMessageSerializer<MessageMMOMusic> {
		
		@Override
		public void encode(MessageMMOMusic message, PacketBuffer buffer) {
			buffer.writeBoolean(message.stop);
			buffer.writeBoolean(message.loop);
			
			boolean nonNull = message.music != null;
			buffer.writeBoolean(nonNull);
			if(nonNull) buffer.writeResourceLocation(message.music.name);
		}

		@Override
		public MessageMMOMusic decode(PacketBuffer buffer) {
			boolean stop = buffer.readBoolean();
			boolean loop = buffer.readBoolean();
			boolean nonNull = buffer.readBoolean();
			SoundEvent music = null;
			if(nonNull) music = new SoundEvent(buffer.readResourceLocation());
			return new MessageMMOMusic(music, stop, loop);
		}

		@Override
		public void handle(MessageMMOMusic message, Supplier<Context> supplier) {
			supplier.get().setPacketHandled(true);
			if(FMLEnvironment.dist != Dist.CLIENT) return;
			
			message.setLoop();
			if(message.stop) message.stopMusic();
			else message.playMusic();
		}
		
	}

}
