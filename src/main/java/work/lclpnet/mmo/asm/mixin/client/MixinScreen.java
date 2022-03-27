package work.lclpnet.mmo.asm.mixin.client;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import work.lclpnet.mmo.asm.type.client.IScreen;

import java.util.List;

@Mixin(Screen.class)
public class MixinScreen implements IScreen {

    @Shadow @Final private List<Drawable> drawables;

    @Shadow @Final private List<Element> children;

    @Shadow @Final private List<Selectable> selectables;

    /**
     * Hacky way to invoke addDrawableChild, since mixin refmap would not generate mapping properly :(
     */
    @Unique
    @Override
    public <T extends Element & Drawable & Selectable> T lclpmmo$addDrawableChild(T child) {
        this.drawables.add(child);
        this.children.add(child);
        this.selectables.add(child);
        return child;
    }
}
