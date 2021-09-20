package work.lclpnet.mmo.facade;

import work.lclpnet.lclpnetwork.facade.User;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.character.MMOCharacter;

public class DummyMMOUser implements IMMOUser {

    private User user = null;
    private MMOCharacter character = null;

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public MMOCharacter getMMOCharacter() {
        return character;
    }

    @Override
    public void setMMOCharacter(MMOCharacter character) {
        this.character = character;
    }
}
