package work.lclpnet.mmo.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IMMOAttacker {

    /**
     * Fired when the entity attacks another entity.
     * This method is client only, if look for the server method: {@link net.minecraft.entity.LivingEntity#attackEntityAsMob(Entity)}.
     *
     * @param victim The entity that was attacked.
     */
    @OnlyIn(Dist.CLIENT)
    void onMMOAttack(Entity victim);
}
