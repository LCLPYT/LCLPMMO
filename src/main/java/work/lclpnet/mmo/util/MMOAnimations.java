package work.lclpnet.mmo.util;

import net.minecraft.entity.Entity;
import work.lclpnet.mmo.entity.IEntitySyncable;
import work.lclpnet.mmo.network.packet.MMOEntityAnimationPacket;
import work.lclpnet.mmocontent.networking.MMONetworking;

public class MMOAnimations {

    public static <T extends Entity & IEntitySyncable> void syncEntityAnimation(T entity, short animationId) {
        MMONetworking.sendToAllTracking(entity, new MMOEntityAnimationPacket(entity, animationId));
    }
}
