package work.lclpnet.mmo.network;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3d;

public class MMODataSerializers {

    public static final TrackedDataHandler<Vec3d> VEC3D = new TrackedDataHandler<Vec3d>() {

        public void write(PacketByteBuf packetByteBuf, Vec3d value) {
            boolean nonNull = value != null;
            packetByteBuf.writeBoolean(nonNull);

            if (nonNull) {
                packetByteBuf.writeDouble(value.x);
                packetByteBuf.writeDouble(value.y);
                packetByteBuf.writeDouble(value.z);
            }
        }

        public Vec3d read(PacketByteBuf packetByteBuf) {
            if (!packetByteBuf.readBoolean()) return null;

            double x = packetByteBuf.readDouble();
            double y = packetByteBuf.readDouble();
            double z = packetByteBuf.readDouble();

            return new Vec3d(x, y, z);
        }

        public Vec3d copy(Vec3d value) {
            return value;
        }
    };

    public static void registerSerializers() {
        register(VEC3D);
    }

    static void register(TrackedDataHandler<?> handler) {
        TrackedDataHandlerRegistry.register(handler);
    }
}
