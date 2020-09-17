package work.lclpnet.mmo.event.custom;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.UUID;

public class MCLinkTokenReceivedEvent extends PlayerEvent {

    protected final String token;

    public MCLinkTokenReceivedEvent(ServerPlayerEntity player, String token) {
        super(player);
        this.token = token;
    }

    public ServerPlayerEntity getServerPlayer() {
        return (ServerPlayerEntity) this.getPlayer();
    }

    public String getToken() {
        return token;
    }
}
