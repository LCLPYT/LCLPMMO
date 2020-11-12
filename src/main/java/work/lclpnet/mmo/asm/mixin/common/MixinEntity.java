package work.lclpnet.mmo.asm.mixin.common;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(Entity.class)
public class MixinEntity {

	@Shadow
	@Final
	private EntityType<?> type;
	
	@Redirect(
			method = "Lnet/minecraft/entity/Entity;getSize(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/EntityType;getSize()Lnet/minecraft/entity/EntitySize;"
					)
			)
	public EntitySize getSize(EntityType<?> type) {
		Entity entity = (Entity) ((Object) this);
		return this.type.getSize().scale(MMOMonsterAttributes.getScaleWidth(entity), MMOMonsterAttributes.getScaleHeight(entity));
	}
	
	@Inject(
			method = "Lnet/minecraft/entity/Entity;getJumpFactor()F",
			at = @At("RETURN"),
			cancellable = true
			)
	public void onGetJumpFactor(CallbackInfoReturnable<Float> cir) {
		float scaleHeight = MMOMonsterAttributes.getScaleHeight((Entity) ((Object) this));
		if(scaleHeight <= 1F || !((Object) this instanceof PlayerEntity)) return;
		cir.setReturnValue(cir.getReturnValue() * scaleHeight);
		cir.cancel();
	}
	
}
