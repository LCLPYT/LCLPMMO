package work.lclpnet.mmo.network.msg;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.corebase.util.MessageType;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MessageMusic implements IMessage {

	public MusicAction action;
	public String file;
	public float volume;
	
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
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(this::handleMusic);		
	}
	
	public void handleMusic() {
		final Consumer<ITextComponent> feedback = text -> Minecraft.getInstance().ingameGUI.func_238450_a_(ChatType.SYSTEM, text, Util.DUMMY_UUID);
		
		switch (action) {
		case PLAY:
			MusicSystem.play(file, feedback);
			break;
		case PLAY_YT:
			if(file.startsWith("url:")) {
				try {
					new URL(file);
					MusicSystem.playYtUrl(file.substring("url:".length()), feedback);
				} catch (MalformedURLException e) {
					feedback.accept(LCLPMMO.TEXT.message(I18n.format("url.malformed"), MessageType.ERROR));
				}
			}
			else if(file.startsWith("search:")) MusicSystem.playYtSearch(file.substring("search:".length()), feedback);
			else if(file.startsWith("downloaded:")) MusicSystem.playDownloaded(file.substring("downloaded:".length()), feedback);
			break;
		case VOLUME:
			if(file.isEmpty()) MusicSystem.setOverallVolume(volume, feedback);
			else MusicSystem.setVolume(file, volume, feedback);
			break;
		case STOP:
			if(file.isEmpty()) MusicSystem.stopAllSound(feedback);
			else MusicSystem.stopSound(file, feedback);
			break;

		default:
			break;
		}
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	public enum MusicAction {
		PLAY, PLAY_YT, VOLUME, STOP
	}
	
	public static class Serializer implements IMessageSerializer<MessageMusic> {
		
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

	}
	
}
