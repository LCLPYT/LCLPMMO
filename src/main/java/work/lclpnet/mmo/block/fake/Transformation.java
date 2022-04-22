package work.lclpnet.mmo.block.fake;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import work.lclpnet.mmo.util.NBTHelper;

import javax.annotation.Nullable;

public class Transformation {

    public static final byte
            TRANSLATE = 0,
            ROTATE    = 1,
            SCALE     = 2;

    public final byte type;
    public final Vec3f value;

    public Transformation(byte type, Vec3f value) {
        this.type = type;
        this.value = value;
    }

    public void apply(MatrixStack matrices, @Nullable Vec3f pivot) {
        switch (type) {
            case TRANSLATE -> matrices.translate(value.getX(), value.getY(), value.getZ());
            case SCALE -> matrices.scale(value.getX(), value.getY(), value.getZ());
            case ROTATE -> {
                if (pivot != null)
                    matrices.translate(pivot.getX(), pivot.getY(), pivot.getZ());

                matrices.multiply(Quaternion.fromEulerXyz(value));

                if (pivot != null)
                    matrices.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
    }

    public void saveNbt(NbtCompound nbt) {
        nbt.putByte("Type", type);
        nbt.put("Value", NBTHelper.serializeVec3f(value));
    }

    public static Transformation loadFromNbt(NbtCompound nbt) {
        if (!nbt.contains("Type")) return null;
        byte type = nbt.getByte("Type");

        NbtElement valueNbt = nbt.get("Value");
        if (!(valueNbt instanceof NbtList)) return null;
        Vec3f value = NBTHelper.deserializeVec3f((NbtList) valueNbt);

        return new Transformation(type, value);
    }
}
