package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.User;

public interface IMMOUser {

	User getUser();
	
	void setUser(User user);
	
	MMOCharacter getMMOCharacter();
	
	void setMMOCharacter(MMOCharacter character);
	
	public static IMMOUser getMMOUser(PlayerEntity player) {
		return (IMMOUser) player;
	}
	
	public static void initMyPlayer(PlayerEntity player) {
		IMMOUser user = (IMMOUser) player;
		user.setUser(User.getCurrent());
		user.setMMOCharacter(User.getSelectedCharacter());
	}
	
}