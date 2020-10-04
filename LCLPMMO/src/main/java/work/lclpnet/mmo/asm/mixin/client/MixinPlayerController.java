package work.lclpnet.mmo.asm.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stats.StatisticsManager;
import work.lclpnet.mmo.asm.type.IMMOUser;

@Mixin(PlayerController.class)
public class MixinPlayerController {

	@Inject(
			method = "Lnet/minecraft/client/multiplayer/PlayerController;createPlayer("
					+ "Lnet/minecraft/client/world/ClientWorld;"
					+ "Lnet/minecraft/stats/StatisticsManager;"
					+ "Lnet/minecraft/client/util/ClientRecipeBook;"
					+ ")Lnet/minecraft/client/entity/player/ClientPlayerEntity;",
					at = @At("RETURN")
			)
	public void onCreatePlayer(ClientWorld world, StatisticsManager stats, ClientRecipeBook recipes, CallbackInfoReturnable<ClientPlayerEntity> cir) {
		IMMOUser.initMyPlayer(cir.getReturnValue());
	}
	
}
