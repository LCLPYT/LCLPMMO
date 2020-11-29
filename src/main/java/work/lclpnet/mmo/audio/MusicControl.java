package work.lclpnet.mmo.audio;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvent;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageMMOMusic;

public class MusicControl {

	public static void playBackgroundMusicFor(ServerPlayerEntity player, SoundEvent sound) {
		playBackgroundMusicFor(player, sound, false);
	}
	
	public static void playBackgroundMusicFor(ServerPlayerEntity player, SoundEvent sound, boolean loop) {
		MessageMMOMusic msg = new MessageMMOMusic(sound, false, loop);
		MMOPacketHandler.sendToClient(player, msg);
	}
	
	public static void stopBackgroundMusicFor(ServerPlayerEntity player) {
		MessageMMOMusic msg = new MessageMMOMusic(null, true, false);
		MMOPacketHandler.sendToClient(player, msg);
	}
	
	public static void setLoopFor(ServerPlayerEntity player, boolean loop) {
		MessageMMOMusic msg = new MessageMMOMusic(null, false, loop);
		MMOPacketHandler.sendToClient(player, msg);
	}
	
}
