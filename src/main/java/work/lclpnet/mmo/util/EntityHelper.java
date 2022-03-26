package work.lclpnet.mmo.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class EntityHelper {

    public static void teleport(Entity entityIn, ServerWorld worldIn, double x, double y, double z, float yaw, float pitch) {
        teleport(entityIn, worldIn, x, y, z, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), yaw, pitch);
    }

    public static void teleport(Entity target, ServerWorld world, double x, double y, double z, Set<PlayerPositionLookS2CPacket.Flag> flags, float yaw, float pitch) {
        if (target instanceof ServerPlayerEntity) {
            ChunkPos chunkPos = new ChunkPos(new BlockPos(x, y, z));
            world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, target.getId());
            target.stopRiding();
            if (((ServerPlayerEntity) target).isSleeping()) {
                ((ServerPlayerEntity) target).wakeUp(true, true);
            }

            if (world == target.world) {
                ((ServerPlayerEntity) target).networkHandler.requestTeleport(x, y, z, yaw, pitch, flags);
            } else {
                ((ServerPlayerEntity) target).teleport(world, x, y, z, yaw, pitch);
            }

            target.setHeadYaw(yaw);
        } else {
            float f = MathHelper.wrapDegrees(yaw);
            float g = MathHelper.wrapDegrees(pitch);
            g = MathHelper.clamp(g, -90.0F, 90.0F);
            if (world == target.world) {
                target.refreshPositionAndAngles(x, y, z, f, g);
                target.setHeadYaw(f);
            } else {
                target.detach();
                Entity entity = target;
                target = target.getType().create(world);
                if (target == null) {
                    return;
                }

                target.copyFrom(entity);
                target.refreshPositionAndAngles(x, y, z, f, g);
                target.setHeadYaw(f);
                world.onDimensionChanged(target);
                entity.discard();
            }
        }

        if (!(target instanceof LivingEntity) || !((LivingEntity) target).isFallFlying()) {
            target.setVelocity(target.getVelocity().multiply(1.0D, 0.0D, 1.0D));
            target.setOnGround(true);
        }

        if (target instanceof PathAwareEntity) {
            ((PathAwareEntity) target).getNavigation().stop();
        }
    }

    public static void teleportToEntity(Collection<? extends Entity> entitys, Entity destination) {
        entitys.forEach((e) -> teleportToEntity(e, destination));
    }

    public static void teleportToEntity(Entity which, Entity destination) {
        teleport(which, (ServerWorld) destination.world, destination.getX(), destination.getY(), destination.getZ(), EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), destination.getYaw(), destination.getPitch());
    }
}
