package work.lclpnet.mmo.block.fake;

import net.minecraft.util.math.BlockPos;

public class FakeBlockPos {

    public static byte byteBounds(int i) {
        if (i > Byte.MAX_VALUE || i < Byte.MIN_VALUE) throw new IllegalArgumentException("Value out of byte boundaries");
        return (byte) i;
    }

    protected byte x, y, z;

    public FakeBlockPos(int x, int y, int z) {
        this(byteBounds(x), byteBounds(y), byteBounds(z));
    }

    public FakeBlockPos(byte x, byte y, byte z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public byte getZ() {
        return z;
    }

    public BlockPos toBlockPos(BlockPos origin) {
        return origin.add(x, y, z);
    }

    public int asInt() {
        return FakeBlockPos.asInt(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public String toString() {
        return "FakeBlockPos{x=%s, y=%s, z=%s}".formatted(x, y, z);
    }

    private static final int SIZE_BITS_X;
    private static final int SIZE_BITS_Z;
    public static final int SIZE_BITS_Y;
    private static final int BITS_X;
    private static final int BITS_Y;
    private static final int BITS_Z;
    private static final int BIT_SHIFT_Z;
    private static final int BIT_SHIFT_X;

    static {
        SIZE_BITS_Z = SIZE_BITS_X = 8;  // max 256
        SIZE_BITS_Y = 32 - SIZE_BITS_X - SIZE_BITS_Z;
        BITS_X = (1 << SIZE_BITS_X) - 1;
        BITS_Y = (1 << SIZE_BITS_Y) - 1;
        BITS_Z = (1 << SIZE_BITS_Z) - 1;
        BIT_SHIFT_Z = SIZE_BITS_Y;
        BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;
    }

    public static byte unpackLongX(int packedPos) {
        return (byte) (packedPos << 32 - BIT_SHIFT_X - SIZE_BITS_X >> 32 - SIZE_BITS_X);
    }

    public static byte unpackLongY(int packedPos) {
        return (byte) (packedPos << 32 - SIZE_BITS_Y >> 32 - SIZE_BITS_Y);
    }

    public static byte unpackLongZ(int packedPos) {
        return (byte) (packedPos << 32 - BIT_SHIFT_Z - SIZE_BITS_Z >> 32 - SIZE_BITS_Z);
    }

    public static FakeBlockPos fromLong(int packedPos) {
        return new FakeBlockPos(FakeBlockPos.unpackLongX(packedPos), FakeBlockPos.unpackLongY(packedPos), FakeBlockPos.unpackLongZ(packedPos));
    }

    public static int asInt(byte x, byte y, byte z) {
        int l = 0;
        l |= ((int) x & BITS_X) << BIT_SHIFT_X;
        l |= ((int) y & BITS_Y);
        return l | ((int) z & BITS_Z) << BIT_SHIFT_Z;
    }
}
