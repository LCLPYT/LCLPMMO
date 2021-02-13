package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.mmo.asm.type.IMMOUser;

import java.util.Optional;

@Mixin(PlayerList.class)
public class MixinPlayerList {

	@Inject(
			method = "Lnet/minecraft/server/management/PlayerList;func_232644_a_("
					+ "Lnet/minecraft/entity/player/ServerPlayerEntity;"
					+ "Z"
					+ ")Lnet/minecraft/entity/player/ServerPlayerEntity;",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/entity/player/ServerPlayerEntity;copyFrom(Lnet/minecraft/entity/player/ServerPlayerEntity;Z)V"
							),
					locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onRecreatePlayerEntity(ServerPlayerEntity p_232644_1_, boolean p_232644_2_, CallbackInfoReturnable<ServerPlayerEntity> cir, 
			BlockPos blockpos, float f, boolean flag, ServerWorld serverworld, Optional<Vector3d> optional, ServerWorld serverworld1, 
			PlayerInteractionManager playerinteractionmanager, ServerPlayerEntity serverplayerentity) {
		IMMOUser mmoOld = IMMOUser.getMMOUser(p_232644_1_), mmoNew = IMMOUser.getMMOUser(serverplayerentity);
		mmoNew.setMMOCharacter(mmoOld.getMMOCharacter());
		mmoNew.setUser(mmoOld.getUser());
	}
	
}
