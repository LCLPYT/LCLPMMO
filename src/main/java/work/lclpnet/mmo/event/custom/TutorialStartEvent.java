package work.lclpnet.mmo.event.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class TutorialStartEvent extends Event {

	private final PlayerEntity player;
	
	public TutorialStartEvent(PlayerEntity player) {
		this.player = player;
	}
	
	/**
	 * @return The player that started the intro. Returns null on client.
	 */
	public PlayerEntity getPlayer() {
		return player;
	}
	
}
