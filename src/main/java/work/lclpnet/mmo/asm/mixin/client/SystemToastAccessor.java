package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SystemToast.class)
public interface SystemToastAccessor {

    @Accessor
    void setTitle(Text text);

    @Accessor
    void setLines(List<OrderedText> lines);

    @Accessor
    void setJustUpdated(boolean justUpdated);

    @Accessor
    void setWidth(int width);
}
