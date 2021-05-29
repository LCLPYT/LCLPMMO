package work.lclpnet.mmo.event.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class DialogCompleteEvent extends PlayerEvent {

    private final int id;

    public DialogCompleteEvent(PlayerEntity player, int id) {
        super(player);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
