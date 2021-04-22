package work.lclpnet.mmo.network.msg;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import work.lclpnet.mmo.entity.IEntityAnimatable;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

import java.util.function.Supplier;

public class MessageEntityAnimation implements IMessage {

    private final int entityId;
    private final short animationId;

    public MessageEntityAnimation(Entity entity, short animationId) {
        this(entity.getEntityId(), animationId);
    }

    public MessageEntityAnimation(int entityId, short animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if(FMLEnvironment.dist != Dist.CLIENT) return;

        World w = Minecraft.getInstance().world;
        if(w == null) return;

        Entity en = w.getEntityByID(this.entityId);
        if(!(en instanceof IEntityAnimatable)) return;

        ((IEntityAnimatable) en).onAnimation(this.animationId);
    }

    public static class Serializer implements IMessageSerializer<MessageEntityAnimation> {

        @Override
        public void encode(MessageEntityAnimation message, PacketBuffer buffer) {
            buffer.writeInt(message.entityId);
            buffer.writeShort(message.animationId);
        }

        @Override
        public MessageEntityAnimation decode(PacketBuffer buffer) {
            int entityId = buffer.readInt();
            short animationId = buffer.readShort();
            return new MessageEntityAnimation(entityId, animationId);
        }
    }

}
