package work.lclpnet.mmo.util;

import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3f;

import javax.annotation.Nullable;

public class NBTHelper {

    public static NbtList serializeVec3f(Vec3f vec) {
        NbtList nbtList = new NbtList();

        nbtList.add(NbtFloat.of(vec.getX()));
        nbtList.add(NbtFloat.of(vec.getY()));
        nbtList.add(NbtFloat.of(vec.getZ()));

        return nbtList;
    }

    @Nullable
    public static Vec3f deserializeVec3f(NbtList nbt) {
        if (nbt.size() != 3) return null;

        return new Vec3f(
                nbt.getFloat(0),
                nbt.getFloat(1),
                nbt.getFloat(2)
        );
    }
}
