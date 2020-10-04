package work.lclpnet.mmo.asm.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import work.lclpnet.mmo.asm.type.IMMOUser;

@Mixin(PlayerList.class)
public class MixinPlayerList {

	@Inject(
			method = "Lnet/minecraft/server/management/PlayerList;recreatePlayerEntity("
					+ "Lnet/minecraft/entity/player/ServerPlayerEntity;"
					+ "Lnet/minecraft/world/dimension/DimensionType;"
					+ "Z"
					+ ")Lnet/minecraft/entity/player/ServerPlayerEntity;",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/entity/player/ServerPlayerEntity;copyFrom(Lnet/minecraft/entity/player/ServerPlayerEntity;Z)V"
							),
					locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onRecreatePlayerEntity(ServerPlayerEntity playerIn, DimensionType dimension, boolean conqueredEnd, 
			CallbackInfoReturnable<ServerPlayerEntity> cir, ServerWorld world, BlockPos blockpos, boolean flag, 
			PlayerInteractionManager playerinteractionmanager, ServerPlayerEntity serverplayerentity) {
		IMMOUser mmoOld = IMMOUser.getMMOUser(playerIn), mmoNew = IMMOUser.getMMOUser(serverplayerentity);
		mmoNew.setMMOCharacter(mmoOld.getMMOCharacter());
		mmoNew.setUser(mmoOld.getUser());
	}
	
}
