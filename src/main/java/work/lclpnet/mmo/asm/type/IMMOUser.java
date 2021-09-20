package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.lclpnetwork.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.network.LCLPNetwork;

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
        user.setUser(LCLPNetwork.getUser());
        user.setMMOCharacter(LCLPNetwork.getSelectedCharacter());
    }
}
