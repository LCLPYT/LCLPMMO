package work.lclpnet.mmo.asm.type;

import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.lclpnetwork.model.User;
import work.lclpnet.mmo.data.character.MMOCharacter;

public interface IMMOUser {

    User getUser();

    void setUser(User user);

    MMOCharacter getMMOCharacter();

    void setMMOCharacter(MMOCharacter character);

    static IMMOUser of(PlayerEntity player) {
        return (IMMOUser) player;
    }
}
