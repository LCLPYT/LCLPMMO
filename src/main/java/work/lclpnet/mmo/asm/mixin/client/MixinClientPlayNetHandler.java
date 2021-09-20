package work.lclpnet.mmo.asm.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.entity.MMOMonsterAttributes;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.network.msg.MessageDisconnectMe;
import work.lclpnet.mmo.util.network.MMOAPI;

import java.util.Iterator;

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
        if (attr.equals(MMOMonsterAttributes.SCALE_HEIGHT) || attr.equals(MMOMonsterAttributes.SCALE_WIDTH))
            entity.recalculateSize();
    }

    @Inject(
            method = "handleSpawnPlayer(Lnet/minecraft/network/play/server/SSpawnPlayerPacket;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/world/ClientWorld;addPlayer("
                            + "ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;"
                            + ")V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onHandleSpawnPlayer(SSpawnPlayerPacket packetIn, CallbackInfo ci, double d0, double d1, double d2, float f, float f1, int i,
                                    RemoteClientPlayerEntity remoteclientplayerentity) {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", remoteclientplayerentity.getGameProfile().getId().toString());

        IMMOUser mmo = IMMOUser.getMMOUser(remoteclientplayerentity);
        MMOAPI.PUBLIC.getActiveCharacterByUuid(remoteclientplayerentity.getGameProfile().getId().toString(), true)
                .thenCompose(character -> {
                    mmo.setMMOCharacter(character);
                    return MMOAPI.PUBLIC.getUserById(character.owner);
                })
                .thenAccept(mmo::setUser)
                .exceptionally(err -> {
                    MMOPacketHandler.INSTANCE.sendToServer(new MessageDisconnectMe(new TranslationTextComponent("mmo.player_load_failed")));
                    return null;
                });
    }
}
