package work.lclpnet.mmo.client.gui.select;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface IMMOSelectionItem {

    Text getTitle();

    /**
     * Provide some kind of language independent string, used to compare items.
     * The string should be unique for the item.
     *
     * @return Some kind of unlocalized string, unique for the item.
     */
    String getUnlocalizedName();

    default Identifier getIcon() {
        return null;
    }

    default String getFirstLine() {
        return null;
    }

    default String getSecondLine() {
        return null;
    }

    default Text getToolTip() {
        return null;
    }
}
