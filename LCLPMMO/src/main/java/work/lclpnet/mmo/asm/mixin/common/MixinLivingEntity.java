package work.lclpnet.mmo.asm.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Inject(
			method = "Lnet/minecraft/entity/LivingEntity;registerAttributes()V",
			at = @At("TAIL")
			)
	protected void onRegisterAttributes(CallbackInfo ci) {
		LivingEntity livingEntity = (LivingEntity) ((Object) this);
		livingEntity.getAttributes().registerAttribute(MMOMonsterAttributes.SCALE_WIDTH);
		livingEntity.getAttributes().registerAttribute(MMOMonsterAttributes.SCALE_HEIGHT);
	}
	
	@Inject(
			method = "Lnet/minecraft/entity/LivingEntity;readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/SharedMonsterAttributes;readAttributes("
							+ "Lnet/minecraft/entity/ai/attributes/AbstractAttributeMap;"
							+ "Lnet/minecraft/nbt/ListNBT;"
							+ ")V",
							shift = Shift.AFTER
					)
			)
	public void onReadAttributes(CompoundNBT nbt, CallbackInfo ci) {
		((LivingEntity) (Object) this).recalculateSize();
	}
	
	@Inject(
			method = "Lnet/minecraft/entity/LivingEntity;calculateFallDamage(FF)I",
			at = @At("RETURN"),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onCalculateFallDamage(float distance, float damageMultiplier, CallbackInfoReturnable<Integer> cir, 
			EffectInstance effectinstance, float f) {
		float scaleHeight = MMOMonsterAttributes.getScaleHeight((LivingEntity) ((Object) this));
		if(scaleHeight == 1F) return;
		
		float heightNullifier = scaleHeight * scaleHeight + 2;
		cir.setReturnValue(MathHelper.ceil((distance - heightNullifier - f) * damageMultiplier));
		cir.cancel();
	}
	
}
