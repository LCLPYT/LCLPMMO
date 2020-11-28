package work.lclpnet.mmo.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DataSerializerEntry;
import work.lclpnet.mmo.LCLPMMO;

@EventBusSubscriber(bus = Bus.MOD, modid = LCLPMMO.MODID)
public class MMODataSerializers {

	private static final List<DataSerializerEntry> ENTRIES = new ArrayList<>();
	
	public static final IDataSerializer<Vector3d> VECTOR_3D = register(MMONames.DataSerializer.VECTOR_3D, new IDataSerializer<Vector3d>() {
		
		public void write(PacketBuffer buf, Vector3d value) {
			buf.writeDouble(value.x);
			buf.writeDouble(value.y);
			buf.writeDouble(value.z);
		}

		public Vector3d read(PacketBuffer buf) {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();
			return new Vector3d(x, y, z);
		}

		public Vector3d copyValue(Vector3d value) {
			return value;
		}
		
	});
	
	private static <T> IDataSerializer<T> register(String name, IDataSerializer<T> serializer) {
		DataSerializerEntry entry = new DataSerializerEntry(serializer);
		entry.setRegistryName(name);
		ENTRIES.add(entry);
		return serializer;
	}
	
	@SubscribeEvent
	public static void registerMMOSerializers(RegistryEvent.Register<DataSerializerEntry> e) {
		ENTRIES.forEach(e.getRegistry()::register);
		ENTRIES.clear();
	}

}
