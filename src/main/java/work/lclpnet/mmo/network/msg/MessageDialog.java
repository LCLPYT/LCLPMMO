package work.lclpnet.mmo.network.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.facade.dialog.DialogData;
import work.lclpnet.mmo.facade.dialog.DialogFragment;
import work.lclpnet.mmo.gui.dialog.DialogScreen;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

public class MessageDialog implements IMessage {

	private int entityId;
	private DialogData data;
	private boolean dismissable;
	
	public MessageDialog(int entityId, DialogData data) {
		this(entityId, data, true);
	}
	
	public MessageDialog(int entityId, DialogData data, boolean dismissable) {
		this.entityId = entityId;
		this.data = data;
		this.dismissable = dismissable;
	}
	
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(this::handleClient);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void handleClient() {
		Minecraft mc = Minecraft.getInstance();
		World w = mc.world;
		Entity en = w.getEntityByID(this.entityId);
		mc.displayGuiScreen(new DialogScreen<>((LivingEntity) en, this.data, this.dismissable));
	}
	
	public static class Serializer implements IMessageSerializer<MessageDialog> {

		@Override
		public void encode(MessageDialog message, PacketBuffer buffer) {
			buffer.writeInt(message.entityId);
			
			List<DialogFragment> structure = message.data.getStructure();
			buffer.writeInt(structure.size());
			structure.forEach(f -> DialogFragment.Serializer.serialize(f, buffer));
			
			buffer.writeBoolean(message.dismissable);
		}
		
		@Override
		public MessageDialog decode(PacketBuffer buffer) {
			int entityId = buffer.readInt();
			
			List<DialogFragment> structure = new ArrayList<>();
			int size = buffer.readInt();
			for (int i = 0; i < size; i++) 
				structure.add(DialogFragment.Serializer.deserialize(buffer));
			DialogData data = new DialogData(structure);
			
			boolean dismissable = buffer.readBoolean();
			
			return new MessageDialog(entityId, data, dismissable);
		}

	}
	
}
