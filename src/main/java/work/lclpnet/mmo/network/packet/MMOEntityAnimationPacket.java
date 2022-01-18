package work.lclpnet.mmo.network.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.entity.IEntitySyncable;
import work.lclpnet.mmocontent.networking.IClientPacketHandler;
import work.lclpnet.mmocontent.networking.IPacketDecoder;
import work.lclpnet.mmocontent.networking.MCPacket;

public class MMOEntityAnimationPacket extends MCPacket implements IClientPacketHandler {

    public static final Identifier ID = LCLPMMO.identifier("entity_animation");

    private final int entityId;
    private final int animationId;

    public <T extends Entity & IEntitySyncable> MMOEntityAnimationPacket(T entity, int animationId) {
        this(entity.getEntityId(), animationId);
    }

    public MMOEntityAnimationPacket(int entityId, int animationId) {
        super(ID);
        this.entityId = entityId;
        this.animationId = animationId;
    }

    @Override
    public void handleClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender sender) {
        ClientWorld world = handler.getWorld();
        if (world == null) return;

        Entity entity = world.getEntityById(entityId);
        if (entity instanceof IEntitySyncable)
            ((IEntitySyncable) entity).onEntityAnimationSync(animationId);
    }

    @Override
    public void encodeTo(PacketByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeShort(animationId);
    }

    public static class Decoder implements IPacketDecoder<MMOEntityAnimationPacket> {

        @Override
        public MMOEntityAnimationPacket decode(PacketByteBuf buffer) {
            int entityId = buffer.readVarInt();
            short animationId = buffer.readShort();
            return new MMOEntityAnimationPacket(entityId, animationId);
        }
    }
}
