package work.lclpnet.mmo.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface MMOSelectionItem {

    ITextComponent getTitle();

    /**
     * Provide some kind of language independent string, used to compare items.
     * The string should be unique for the item.
     * @return Some kind of unlocalized string, unique for the item.
     */
    String getUnlocalizedName();

    default ResourceLocation getIcon() {
        return null;
    }

    default String getFirstLine() {
        return null;
    }

    default String getSecondLine() {
        return null;
    }

    default ITextComponent getToolTip() {
        return null;
    }
}
