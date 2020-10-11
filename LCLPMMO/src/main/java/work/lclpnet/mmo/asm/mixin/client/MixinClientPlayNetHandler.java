package work.lclpnet.mmo.asm.mixin.client;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.gson.JsonObject;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageDisconnectMe;
import work.lclpnet.mmo.util.LCLPNetwork;
import work.lclpnet.mmo.util.MMOMonsterAttributes;
import work.lclpnet.mmo.util.User;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler {

	@Inject(
			method = "Lnet/minecraft/client/network/play/ClientPlayNetHandler;handleEntityProperties("
					+ "Lnet/minecraft/network/play/server/SEntityPropertiesPacket;"
					+ ")V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/entity/ai/attributes/ModifiableAttributeInstance;setBaseValue(D)V",
							shift = Shift.AFTER
							),
					locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onSetBaseValue(SEntityPropertiesPacket packetIn, CallbackInfo ci, 
			Entity entity, AttributeModifierManager abstractattributemap, Iterator<SEntityPropertiesPacket> iterator, 
			SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot, ModifiableAttributeInstance iattributeinstance) {
		Attribute attr = iattributeinstance.getAttribute();
		if(attr.equals(MMOMonsterAttributes.SCALE_HEIGHT) || attr.equals(MMOMonsterAttributes.SCALE_WIDTH)) entity.recalculateSize();
	}

	@Redirect(
			method = "Lnet/minecraft/client/network/play/ClientPlayNetHandler;handleSpawnPlayer(Lnet/minecraft/network/play/server/SSpawnPlayerPacket;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/world/ClientWorld;addPlayer("
							+ "ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;"
							+ ")V"
					)
			)
	public void onHandleSpawnPlayer(ClientWorld world, int playerId, AbstractClientPlayerEntity playerEntityIn) {
		JsonObject body = new JsonObject();
		body.addProperty("uuid", playerEntityIn.getGameProfile().getId().toString());

		LCLPNetwork.post("api/ls5/get-active-character-by-uuid", body, response -> {
			final MMOCharacter character = User.handleActiveCharacterResponse(response);
			if(character == null) {
				MMOPacketHandler.INSTANCE.sendToServer(new MessageDisconnectMe(new TranslationTextComponent("mmo.player_load_failed")));
				return;
			}

			JsonObject body2 = new JsonObject();
			body2.addProperty("userId", character.owner);

			LCLPNetwork.post("api/auth/user-by-id", body2, resp -> {
				User user = User.handleUserResponse(resp);
				if(user == null) {
					MMOPacketHandler.INSTANCE.sendToServer(new MessageDisconnectMe(new TranslationTextComponent("mmo.player_load_failed")));
					return;
				}

				IMMOUser mmo = IMMOUser.getMMOUser(playerEntityIn);
				mmo.setUser(user);
				mmo.setMMOCharacter(character);

				world.addPlayer(playerId, playerEntityIn);	
			});
		});
	}

}
