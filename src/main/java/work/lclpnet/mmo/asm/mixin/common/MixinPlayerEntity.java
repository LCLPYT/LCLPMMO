package work.lclpnet.mmo.asm.mixin.common;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkDirection;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.dialog.Dialog;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageDialog;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements IMMOUser, IMMOPlayer {

	private transient User mmoUser = null;
	private transient MMOCharacter mmoCharacter = null;
	private transient Dialog currentMMODialog = null;
	
	/**
	 * This method modifies the scaled (lclpmmo) entity size of a player. 
	 */
	@Inject(
			method = "Lnet/minecraft/entity/player/PlayerEntity;getSize(Lnet/minecraft/entity/Pose;)Lnet/minecraft/entity/EntitySize;",
			at = @At("HEAD"),
			cancellable = true
			)
	public void onGetSize(Pose poseIn, CallbackInfoReturnable<EntitySize> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		
		MMOCharacter character = IMMOUser.getMMOUser(player).getMMOCharacter();
		if(character != null && character.getRace() != null) {
			MMORace r = character.getRace();
			Map<Pose, EntitySize> sizes = r.getEntitySizeOverrides();
			EntitySize size;
			if(sizes != null && (size = sizes.get(poseIn)) != null) {
				cir.setReturnValue(size);
				cir.cancel();
			}
		}
		
		float widthScale = MMOMonsterAttributes.getScaleWidth(player),
				heightScale = MMOMonsterAttributes.getScaleHeight(player);
		if(widthScale == 1F && heightScale == 1F) return;
		
		cir.setReturnValue(cir.getReturnValue().scale(widthScale, heightScale));
		cir.cancel();
	}
	
	@Inject(
			method = "Lnet/minecraft/entity/player/PlayerEntity;getStandingEyeHeight("
					+ "Lnet/minecraft/entity/Pose;"
					+ "Lnet/minecraft/entity/EntitySize;"
					+ ")F",
					at = @At("HEAD"),
					cancellable = true
			)
	public void onGetEyeHeight(Pose pose, EntitySize size, CallbackInfoReturnable<Float> cir) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		
		MMOCharacter character = IMMOUser.getMMOUser(player).getMMOCharacter();
		if(character != null && character.getRace() != null) {
			MMORace r = character.getRace();
			float height = r.getEyeHeightOverride(pose, size);
			if(!Float.isNaN(height)) {
				cir.setReturnValue(height);
				cir.cancel();
			}
		}
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

	@Override
	public void openMMODialog(Dialog dialog) {
		PlayerEntity p = (PlayerEntity) (Object) this;
		if(p instanceof ServerPlayerEntity) {
			ServerPlayerEntity sp = (ServerPlayerEntity) p;
			this.currentMMODialog = dialog; // For server players, the currentMMODialog has to be set here instead of the central (client only) method
			MMOPacketHandler.INSTANCE.sendTo(new MessageDialog(dialog), sp.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		} else {
			openMMODialogOnClient(dialog);
		}
	}

	@Override
	public void setCurrentMMODialog(Dialog dialog) {
		this.currentMMODialog = dialog;
	}
	
	@Override
	public Dialog getCurrentMMODialog() {
		return this.currentMMODialog;
	}

	@Override
	public void closeMMODialog() {
		PlayerEntity p = (PlayerEntity) (Object) this;
		if(p instanceof ServerPlayerEntity) {
			ServerPlayerEntity sp = (ServerPlayerEntity) p;
			this.currentMMODialog = null; // For server players, the currentMMODialog has to be set here instead of the central (client only) method
			MMOPacketHandler.INSTANCE.sendTo(MessageDialog.getCloseMessage(), sp.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		} else {
			closeMMODialogOnClient();
			MMOPacketHandler.INSTANCE.sendToServer(MessageDialog.getCloseMessage());
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void openMMODialogOnClient(Dialog dialog) {
		MessageDialog.openDialog(Minecraft.getInstance(), dialog);
	}
	
	@OnlyIn(Dist.CLIENT)
	private void closeMMODialogOnClient() {
		MessageDialog.closeDialog(Minecraft.getInstance());
	}
	
}
