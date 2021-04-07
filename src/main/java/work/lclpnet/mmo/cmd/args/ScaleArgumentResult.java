package work.lclpnet.mmo.cmd.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector2f;
import work.lclpnet.mmo.entity.MMOMonsterAttributes;

public class ScaleArgumentResult {

    private final ScalePart x, y;

    public ScaleArgumentResult(ScalePart x, ScalePart y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f getScale(Entity source) {
        Vector2f vec3d = MMOMonsterAttributes.getScales(source);
        return new Vector2f(this.x.get(vec3d.x), this.y.get(vec3d.y));
    }

    public boolean isXRelative() {
        return this.x.isRelative();
    }

    public boolean isYRelative() {
        return this.y.isRelative();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof ScaleArgumentResult)) {
            return false;
        } else {
            ScaleArgumentResult scaleArg = (ScaleArgumentResult) obj;
            if (!this.x.equals(scaleArg.x)) {
                return false;
            } else {
                return this.y.equals(scaleArg.y);
            }
        }
    }

    public static ScaleArgumentResult parseInt(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        ScalePart scalepart = ScalePart.parseInt(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            ScalePart scalepart1 = ScalePart.parseInt(reader);
            return new ScaleArgumentResult(scalepart, scalepart1);
        } else {
            reader.setCursor(i);
            throw ScaleArgumentType.VEC2_INCOMPLETE.createWithContext(reader);
        }
    }

    public static ScaleArgumentResult parseFloat(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        ScalePart scalepart = ScalePart.parseFloat(reader);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            ScalePart scalepart1 = ScalePart.parseFloat(reader);
            return new ScaleArgumentResult(scalepart, scalepart1);
        } else {
            reader.setCursor(i);
            throw ScaleArgumentType.VEC2_INCOMPLETE.createWithContext(reader);
        }
    }

    public int hashCode() {
        int i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        return i;
    }

}
