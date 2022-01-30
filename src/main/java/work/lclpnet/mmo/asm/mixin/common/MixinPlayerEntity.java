package work.lclpnet.mmo.asm.mixin.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import work.lclpnet.lclpnetwork.model.User;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.data.dialog.Dialog;
import work.lclpnet.mmo.network.packet.DialogPacket;
import work.lclpnet.mmocontent.networking.MMONetworking;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements IMMOPlayer, IMMOUser {

    @Unique private transient User user = null;
    @Unique private transient MMOCharacter character = null;
    @Unique private transient Dialog currentDialog = null;

    @Override
    public void openMMODialog(Dialog dialog) {
        PlayerEntity p = (PlayerEntity) (Object) this;
        if (p instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) p;
            this.currentDialog = dialog; // For server players, the currentMMODialog has to be set here instead of the central (client only) method

            MMONetworking.sendPacketTo(new DialogPacket(dialog), player);
        } else {
            DialogPacket.openDialogClient(dialog);
        }
    }

    @Override
    public void setCurrentMMODialog(Dialog dialog) {
        this.currentDialog = dialog;
    }

    @Override
    public Dialog getCurrentMMODialog() {
        return currentDialog;
    }

    @Override
    public void closeMMODialog() {
        PlayerEntity p = (PlayerEntity) (Object) this;
        DialogPacket closePacket = DialogPacket.getClosePacket();

        if (p instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) p;
            this.currentDialog = null; // For server players, the currentMMODialog has to be set here instead of the central (client only) method

            MMONetworking.sendPacketTo(closePacket, player);
        } else {
            DialogPacket.closeDialogClient();
            MMONetworking.sendPacketToServer(closePacket);
        }
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public MMOCharacter getMMOCharacter() {
        return this.character;
    }

    @Override
    public void setMMOCharacter(MMOCharacter character) {
        this.character = character;
    }
}
