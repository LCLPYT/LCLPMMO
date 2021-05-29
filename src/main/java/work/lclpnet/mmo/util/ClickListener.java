package work.lclpnet.mmo.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public interface ClickListener<T extends Entity> {

    /**
     * @param entity  The entity that was clicked.
     * @param clicker The player who clicked.
     * @return If the click should be cancelled.
     */
    boolean onClick(T entity, PlayerEntity clicker);
}
