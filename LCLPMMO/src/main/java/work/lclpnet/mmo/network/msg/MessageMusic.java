package work.lclpnet.mmo.network.msg;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.network.IMessage;

public class MessageMusic implements IMessage<MessageMusic>{

	public MusicAction action;
	public String file;
	public float volume;
	
	public MessageMusic() {} //Empty constructor for IMessage
	
	public MessageMusic(MusicAction action) {
		this(action, "", -1F);
	}
	
	public MessageMusic(MusicAction action, String file) {
		this(action, file, -1F);
	}
	
	public MessageMusic(MusicAction action, float volume) {
		this(action, "", volume);
	}
	
	public MessageMusic(MusicAction action, String file, float volume) {
		this.action = action;
		this.file = file;
		this.volume = volume;
	}
	
	@Override
	public void encode(MessageMusic message, PacketBuffer buffer) {
		buffer.writeEnumValue(message.action);
		buffer.writeString(message.file);
		buffer.writeFloat(message.volume);
	}

	@Override
	public MessageMusic decode(PacketBuffer buffer) {
		MusicAction action = buffer.readEnumValue(MusicAction.class);
		String file = buffer.readString();
		float volume = buffer.readFloat();
		return new MessageMusic(action, file, volume);
	}

	@Override
	public void handle(MessageMusic message, Supplier<Context> supplier) {
		supplier.get().enqueueWork(() -> {
			handleMusic(message);
		});
		supplier.get().setPacketHandled(true);
	}
	
	public void handleMusic(MessageMusic msg) {
		final Consumer<ITextComponent> feedback = text -> Minecraft.getInstance().ingameGUI.addChatMessage(ChatType.SYSTEM, text);
		
		switch (msg.action) {
		case PLAY:
			MusicSystem.play(msg.file, feedback);
			break;
		case VOLUME:
			if(msg.file.isEmpty()) MusicSystem.setOverallVolume(msg.volume, feedback);
			else MusicSystem.setVolume(msg.file, msg.volume, feedback);
			break;
		case STOP:
			if(msg.file.isEmpty()) MusicSystem.stopAllSound(feedback);
			else MusicSystem.stopSound(msg.file, feedback);
			break;

		default:
			break;
		}
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public static enum MusicAction {
		PLAY, VOLUME, STOP
	}
	
}
