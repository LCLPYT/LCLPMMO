package work.lclpnet.mmo.network.msg;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import work.lclpnet.mmo.entity.IMMOAttacker;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;
import work.lclpnet.mmo.network.MMOPacketHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class MessageEntityAttack implements IMessage {

    private final int entityId;
    private final Integer victimEntityId;

    public MessageEntityAttack(Entity entity, Entity victim) {
        this(entity.getEntityId(), victim.getEntityId());
    }

    public MessageEntityAttack(int entityId, Integer victimEntityId) {
        this.entityId = entityId;
        this.victimEntityId = victimEntityId;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (FMLEnvironment.dist == Dist.CLIENT) handleClient();
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        World w = Minecraft.getInstance().world;
        if (w == null) return;

        Entity en = w.getEntityByID(this.entityId);
        if (!(en instanceof IMMOAttacker)) return;

        Entity victim = w.getEntityByID(this.victimEntityId);
        ((IMMOAttacker) en).onMMOAttack(victim);
    }

    @ParametersAreNonnullByDefault
    public static <T extends Entity & IMMOAttacker> void sync(T attacker, Entity victim) {
        if (!attacker.world.isRemote)
            MMOPacketHandler.sendToTrackingEntity(attacker, new MessageEntityAttack(attacker, victim));
    }

    public static class Serializer implements IMessageSerializer<MessageEntityAttack> {

        @Override
        public void encode(MessageEntityAttack message, PacketBuffer buffer) {
            buffer.writeInt(message.entityId);
            buffer.writeInt(message.victimEntityId);
        }

        @Override
        public MessageEntityAttack decode(PacketBuffer buffer) {
            int entityId = buffer.readInt();
            int victimId = buffer.readInt();

            return new MessageEntityAttack(entityId, victimId);
        }
    }
}
