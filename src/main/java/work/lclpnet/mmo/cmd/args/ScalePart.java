package work.lclpnet.mmo.cmd.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.util.text.TranslationTextComponent;

public class ScalePart {

	public static final SimpleCommandExceptionType EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.double"));
	public static final SimpleCommandExceptionType EXPECTED_INT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos.missing.int"));
	private final boolean relative;
	private final float value;

	public ScalePart(boolean relativeIn, float valueIn) {
		this.relative = relativeIn;
		this.value = valueIn;
	}

	public float get(float coord) {
		return this.relative ? this.value + coord : this.value;
	}

	public static ScalePart parseFloat(StringReader reader) throws CommandSyntaxException {
		if (reader.canRead() && reader.peek() == '^') {
			throw Vec3Argument.POS_MIXED_TYPES.createWithContext(reader);
		} else if (!reader.canRead()) {
			throw EXPECTED_DOUBLE.createWithContext(reader);
		} else {
			boolean flag = isRelative(reader);
			int i = reader.getCursor();
			float d0 = reader.canRead() && reader.peek() != ' ' ? reader.readFloat() : 0.0F;
			String s = reader.getString().substring(i, reader.getCursor());
			if (flag && s.isEmpty()) {
				return new ScalePart(true, 0.0F);
			} else {
				return new ScalePart(flag, d0);
			}
		}
	}

	public static ScalePart parseInt(StringReader reader) throws CommandSyntaxException {
		if (!reader.canRead()) {
			throw EXPECTED_INT.createWithContext(reader);
		} else {
			boolean flag = isRelative(reader);
			float d0;
			if (reader.canRead() && reader.peek() != ' ') {
				d0 = flag ? reader.readFloat() : (float) reader.readInt();
			} else {
				d0 = 0.0F;
			}

			return new ScalePart(flag, d0);
		}
	}

	private static boolean isRelative(StringReader reader) {
		boolean flag;
		if (reader.peek() == '~') {
			flag = true;
			reader.skip();
		} else {
			flag = false;
		}

		return flag;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof ScalePart)) {
			return false;
		} else {
			ScalePart scalepart = (ScalePart) p_equals_1_;
			if (this.relative != scalepart.relative) {
				return false;
			} else {
				return Float.compare(scalepart.value, this.value) == 0;
			}
		}
	}

	public int hashCode() {
		int i = this.relative ? 1 : 0;
		int j = Float.floatToIntBits(this.value);
		i = 31 * i + (int)(j ^ j >>> 32);
		return i;
	}

	public boolean isRelative() {
		return this.relative;
	}

}
