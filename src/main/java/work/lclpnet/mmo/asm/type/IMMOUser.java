package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;

public interface IMMOUser {

    User getUser();

    void setUser(User user);

    MMOCharacter getMMOCharacter();

    void setMMOCharacter(MMOCharacter character);

    static IMMOUser getMMOUser(PlayerEntity player) {
        return (IMMOUser) player;
    }

    static void initMyPlayer(PlayerEntity player) {
        IMMOUser user = (IMMOUser) player;
        user.setUser(User.getCurrent());
        user.setMMOCharacter(User.getSelectedCharacter());
    }
}
