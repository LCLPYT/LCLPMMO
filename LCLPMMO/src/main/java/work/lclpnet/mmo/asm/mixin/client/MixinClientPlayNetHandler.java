package work.lclpnet.mmo.asm.mixin.client;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import work.lclpnet.mmo.util.MMOMonsterAttributes;

@Mixin(ClientPlayNetHandler.class)
public class MixinClientPlayNetHandler {

	@Inject(
			method = "Lnet/minecraft/client/network/play/ClientPlayNetHandler;handleEntityProperties("
					+ "Lnet/minecraft/network/play/server/SEntityPropertiesPacket;"
					+ ")V",
					at = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;setBaseValue(D)V",
							shift = Shift.AFTER
							),
					locals = LocalCapture.CAPTURE_FAILHARD
			)
	public void onSetBaseValue(SEntityPropertiesPacket packetIn, CallbackInfo ci, 
			Entity entity, AbstractAttributeMap abstractattributemap, Iterator<SEntityPropertiesPacket> iterator, 
			SEntityPropertiesPacket.Snapshot sentitypropertiespacket$snapshot, IAttributeInstance iattributeinstance) {
		IAttribute attr = iattributeinstance.getAttribute();
		if(attr.equals(MMOMonsterAttributes.SCALE_HEIGHT) || attr.equals(MMOMonsterAttributes.SCALE_WIDTH)) entity.recalculateSize();
	}
	
}
