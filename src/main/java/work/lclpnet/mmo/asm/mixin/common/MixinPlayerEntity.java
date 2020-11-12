package work.lclpnet.mmo.asm.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements IMMOUser{

	private transient User mmoUser;
	private transient MMOCharacter mmoCharacter;
	
	/**
	 * This method modifies the scaled (lclpmmo) entity size of a player. 
	 */
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

	@Override
	public User getUser() {
		return mmoUser;
	}

	@Override
	public void setUser(User user) {
		this.mmoUser = user;
	}
	
	@Override
	public MMOCharacter getMMOCharacter() {
		return mmoCharacter;
	}
	
	@Override
	public void setMMOCharacter(MMOCharacter mmoCharacter) {
		this.mmoCharacter = mmoCharacter;
	}
	
}
