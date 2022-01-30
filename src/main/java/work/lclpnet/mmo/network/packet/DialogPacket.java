package work.lclpnet.mmo.network.packet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import work.lclpnet.mmo.LCLPMMO;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.client.gui.dialog.DialogScreen;
import work.lclpnet.mmo.data.dialog.Dialog;
import work.lclpnet.mmo.data.dialog.DialogData;
import work.lclpnet.mmo.data.dialog.DialogFragment;
import work.lclpnet.mmo.event.DialogCompleteCallback;
import work.lclpnet.mmocontent.networking.IClientPacketHandler;
import work.lclpnet.mmocontent.networking.IServerPacketHandler;
import work.lclpnet.mmocontent.networking.MCPacket;
import work.lclpnet.mmocontent.networking.MMONetworking;

import java.util.List;
import java.util.Objects;

public class DialogPacket extends MCPacket implements IClientPacketHandler, IServerPacketHandler {

    public static final Identifier ID = LCLPMMO.identifier("dialog");
    private static final byte ACTION_OPEN = 0, ACTION_CLOSE = 1, ACTION_COMPLETE = 2;

    private final byte action;
    private int id;
    private int entityId;
    private DialogData data;
    private boolean dismissible;

    public DialogPacket(byte action) {
        super(ID);
        this.action = action;
    }

    protected DialogPacket(int id) {
        this(ACTION_COMPLETE);
        this.id = id;
    }

    protected DialogPacket(int id, int entityId, DialogData data, boolean dismissible) {
        this(ACTION_OPEN);
        this.id = id;
        this.entityId = entityId;
        this.data = data;
        this.dismissible = dismissible;
    }

    public DialogPacket(Dialog dialog) {
        this(dialog.getId(), dialog.getPartner().getEntityId(), dialog.getData(), dialog.isDismissable());
    }

    public static DialogPacket getClosePacket() {
        return new DialogPacket(ACTION_CLOSE);
    }

    @Override
    public void encodeTo(PacketByteBuf buffer) {
        buffer.writeByte(this.action);

        switch (this.action) {
            case ACTION_OPEN:
                buffer.writeInt(this.id);
                buffer.writeInt(this.entityId);

                List<DialogFragment> structure = this.data.getStructure();
                buffer.writeInt(structure.size());
                structure.forEach(f -> DialogFragment.Serializer.serialize(f, buffer));

                buffer.writeBoolean(this.dismissible);
                break;
            case ACTION_CLOSE:
                break;
            case ACTION_COMPLETE:
                buffer.writeInt(this.id);
                break;

            default:
                throw new IllegalStateException(String.format("Action %s is unimplemented!", this.action));
        }
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketSender responseSender) {
        switch (this.action) {
            case ACTION_CLOSE:
                IMMOPlayer.of(player).setCurrentMMODialog(null);
                break;
            case ACTION_COMPLETE:
                DialogCompleteCallback.EVENT.invoker().completeDialog(this.id);
                break;

            default:
                throw new IllegalStateException(String.format("Action %s is unimplemented!", this.action));
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void handleClient(MinecraftClient client, ClientPlayNetworkHandler handler, PacketSender sender) {
        switch (this.action) {
            case ACTION_OPEN:
                World w = Objects.requireNonNull(client.world);
                openDialogClient(new Dialog(id, w.getEntityById(this.entityId), this.data).setDismissable(this.dismissible));
                break;
            case ACTION_CLOSE:
                closeDialogClient();
                break;

            default:
                throw new IllegalArgumentException(String.format("Action %s is unimplemented!", this.action));
        }
    }

    @Environment(EnvType.CLIENT)
    public static void closeDialogClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(IMMOPlayer.of(client.player))
                .setCurrentMMODialog(null);
        client.openScreen(null);
    }

    @Environment(EnvType.CLIENT)
    public static void openDialogClient(Dialog dialog) {
        MinecraftClient client = MinecraftClient.getInstance();
        Objects.requireNonNull(IMMOPlayer.of(client.player))
                .setCurrentMMODialog(dialog);
        client.openScreen(new DialogScreen<>(dialog));
    }

    @Environment(EnvType.CLIENT)
    public static void sendDialogCompleteToServer(int id) {
        MMONetworking.sendPacketToServer(new DialogPacket(id));
    }
}
