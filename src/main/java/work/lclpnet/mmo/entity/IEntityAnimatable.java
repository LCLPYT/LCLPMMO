package work.lclpnet.mmo.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageEntityAnimation;

public interface IEntityAnimatable {

    /**
     * Fired on the client when a animation should be played on this entity from the server.
     * @param animationId The id of the animation.
     */
    @OnlyIn(Dist.CLIENT)
    void onAnimation(short animationId);

    default void playAnimation(Entity entity, short animationId) {
        if(entity.world.isRemote) throw new IllegalStateException("Tried to play animation from client.");
        MessageEntityAnimation msg = new MessageEntityAnimation(entity, animationId);
        MMOPacketHandler.sendToTrackingEntity(entity, msg);
    }

}
