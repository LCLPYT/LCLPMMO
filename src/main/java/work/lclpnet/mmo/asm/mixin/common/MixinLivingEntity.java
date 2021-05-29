package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.mmo.entity.MMOMonsterAttributes;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(
            method = "registerAttributes()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void onRegisterAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
        MutableAttribute ma = cir.getReturnValue();
        cir.setReturnValue(ma
                .createMutableAttribute(MMOMonsterAttributes.SCALE_WIDTH)
                .createMutableAttribute(MMOMonsterAttributes.SCALE_HEIGHT));
        cir.cancel();
    }

    @Inject(
            method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/ai/attributes/AttributeModifierManager;deserialize("
                            + "Lnet/minecraft/nbt/ListNBT;"
                            + ")V",
                    shift = Shift.AFTER
            )
    )
    public void onReadAttributes(CompoundNBT nbt, CallbackInfo ci) {
        ((LivingEntity) (Object) this).recalculateSize();
    }

    @Inject(
            method = "calculateFallDamage(FF)I",
            at = @At("RETURN"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onCalculateFallDamage(float distance, float damageMultiplier, CallbackInfoReturnable<Integer> cir,
                                      EffectInstance effectinstance, float f) {
        float scaleHeight = MMOMonsterAttributes.getScaleHeight((LivingEntity) ((Object) this));
        if (scaleHeight == 1F) return;

        float heightNullifier = scaleHeight * scaleHeight + 2;
        cir.setReturnValue(MathHelper.ceil((distance - heightNullifier - f) * damageMultiplier));
        cir.cancel();
    }

    @Inject(
            method = "getEyeHeight(Lnet/minecraft/entity/Pose;Lnet/minecraft/entity/EntitySize;)F",
            at = @At("RETURN"),
            cancellable = true
    )
    public void onGetEyeHeight(Pose poseIn, EntitySize entitySizeIn, CallbackInfoReturnable<Float> cir) {
        LivingEntity le = (LivingEntity) (Object) this;
        float scaleHeight = MMOMonsterAttributes.getScaleHeight(le);
        if (scaleHeight == 1F) return;

        cir.setReturnValue(cir.getReturnValue() * scaleHeight);
        cir.cancel();
    }
}
