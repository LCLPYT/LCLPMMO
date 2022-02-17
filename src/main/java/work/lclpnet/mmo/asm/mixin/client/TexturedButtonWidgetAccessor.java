package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TexturedButtonWidget.class)
public interface TexturedButtonWidgetAccessor {

    @Accessor
    Identifier getTexture();

    @Accessor("u")
    int getU();

    @Accessor("v")
    int getV();

    @Accessor
    int getHoveredVOffset();

    @Accessor
    int getTextureWidth();

    @Accessor
    int getTextureHeight();
}
