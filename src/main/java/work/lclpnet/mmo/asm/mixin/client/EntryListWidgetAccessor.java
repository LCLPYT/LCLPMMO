package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor {

    @Accessor("renderHeader")
    boolean shouldRenderHeader();

    @Accessor("renderSelection")
    boolean shouldRenderSelection();
}
