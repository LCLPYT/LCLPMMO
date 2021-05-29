package work.lclpnet.mmo.entity;

import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * Should be implemented by entities which's tracking behavior should be modified.
 *
 * @author LCLP
 */
public interface ILimitTracking {

    /**
     * Overrides the functionality to track the entity;
     *
     * @param player The player that should the the entity.
     * @return True, if the player should track the entity.
     */
    boolean shouldBeTrackedBy(ServerPlayerEntity player);
}
