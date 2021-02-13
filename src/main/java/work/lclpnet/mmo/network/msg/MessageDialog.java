package work.lclpnet.mmo.network.msg;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.asm.type.IMMOPlayer;
import work.lclpnet.mmo.event.custom.DialogCompleteEvent;
import work.lclpnet.mmo.facade.dialog.Dialog;
import work.lclpnet.mmo.facade.dialog.DialogData;
import work.lclpnet.mmo.facade.dialog.DialogFragment;
import work.lclpnet.mmo.gui.dialog.DialogScreen;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MessageDialog implements IMessage {

	private static final byte ACTION_OPEN = 0, ACTION_CLOSE = 1, ACTION_COMPLETE = 2;

	private byte action;
	private int id;
	private int entityId;
	private DialogData data;
	private boolean dismissable;

	public MessageDialog(Dialog dialog) {
		this(dialog.getId(), dialog.getPartner().getEntityId(), dialog.getData(), dialog.isDismissable());
	}

	protected MessageDialog(int id, int entityId, DialogData data, boolean dismissable) {
		this(ACTION_OPEN);
		this.id = id;
		this.entityId = entityId;
		this.data = data;
		this.dismissable = dismissable;
	}
	
	protected MessageDialog(int id) {
		this(ACTION_COMPLETE);
		this.id = id;
	}
	
	protected MessageDialog(byte action) {
		this.action = action;
	}
	
	public static MessageDialog getCloseMessage() {
		return new MessageDialog(ACTION_CLOSE);
	}
	
	public static MessageDialog getCompleteMessage(int id) {
		return new MessageDialog(id);
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		Context c = ctx.get();
		final ServerPlayerEntity sender = c.getSender();
		c.enqueueWork(sender != null ? () -> handleServer(sender) : this::handleClient);
	}

	public void handleServer(ServerPlayerEntity p) {
		switch (this.action) {
		case ACTION_CLOSE:
			IMMOPlayer.get(p).setCurrentMMODialog(null);
			break;
		case ACTION_COMPLETE:
			MinecraftForge.EVENT_BUS.post(new DialogCompleteEvent(p, this.id));
			break;

		default:
			throw new IllegalStateException("Action " + this.action + " is unimplemented!");
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void handleClient() {
		Minecraft mc = Minecraft.getInstance();
		switch (this.action) {
		case ACTION_OPEN:
			World w = mc.world;
			openDialog(mc, new Dialog(id, w.getEntityByID(this.entityId), this.data).setDismissable(this.dismissable));
			break;
		case ACTION_CLOSE:
			closeDialog(mc);
			break;

		default:
			throw new IllegalArgumentException("Action " + this.action + " is unimplemented!");
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void closeDialog(Minecraft mc) {
		IMMOPlayer.get(mc.player).setCurrentMMODialog(null);
		mc.displayGuiScreen(null);
	}

	@OnlyIn(Dist.CLIENT)
	public static void openDialog(Minecraft mc, Dialog dialog) {
		IMMOPlayer.get(mc.player).setCurrentMMODialog(dialog);
		mc.displayGuiScreen(new DialogScreen<>(dialog));
	}

	public static class Serializer implements IMessageSerializer<MessageDialog> {

		@Override
		public void encode(MessageDialog message, PacketBuffer buffer) {
			buffer.writeByte(message.action);

			switch (message.action) {
			case ACTION_OPEN:
				buffer.writeInt(message.id);
				buffer.writeInt(message.entityId);

				List<DialogFragment> structure = message.data.getStructure();
				buffer.writeInt(structure.size());
				structure.forEach(f -> DialogFragment.Serializer.serialize(f, buffer));

				buffer.writeBoolean(message.dismissable);				
				break;
			case ACTION_CLOSE:
				break;
			case ACTION_COMPLETE:
				buffer.writeInt(message.id);
				break;

			default:
				throw new IllegalStateException("Action " + message.action + " is unimplemented!");
			}
		}

		@Override
		public MessageDialog decode(PacketBuffer buffer) {
			byte action = buffer.readByte();
			
			switch (action) {
			case ACTION_OPEN:
				int id = buffer.readInt();
				int entityId = buffer.readInt();

				List<DialogFragment> structure = new ArrayList<>();
				int size = buffer.readInt();
				for (int i = 0; i < size; i++) 
					structure.add(DialogFragment.Serializer.deserialize(buffer));
				DialogData data = new DialogData(structure);

				boolean dismissable = buffer.readBoolean();

				return new MessageDialog(id, entityId, data, dismissable);
			case ACTION_CLOSE:
				return new MessageDialog(ACTION_CLOSE);
			case ACTION_COMPLETE:
				int completeId = buffer.readInt();
				return new MessageDialog(completeId);

			default:
				throw new IllegalStateException("Action " + action + " is unimplemented!");
			}
		}

	}

}
