package work.lclpnet.mmo.asm.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

	@Inject(
			method = "Lnet/minecraft/entity/player/PlayerEntity;getSize(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;",
			at = @At("RETURN"),
			cancellable = true
			)
	public void onGetSize(Pose poseIn, CallbackInfoReturnable<EntitySize> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		float widthScale = MMOMonsterAttributes.getScaleWidth(player),
				heightScale = MMOMonsterAttributes.getScaleHeight(player);
		if(widthScale == 1F && heightScale == 1F) return;
		
		cir.setReturnValue(cir.getReturnValue().scale(widthScale, heightScale));
		cir.cancel();
	}
	
}
