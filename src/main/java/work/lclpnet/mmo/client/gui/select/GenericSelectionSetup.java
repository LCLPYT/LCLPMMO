package work.lclpnet.mmo.client.gui.select;

import net.minecraft.text.OrderedText;

import java.util.List;

public interface GenericSelectionSetup<T extends IMMOSelectionItem> {

    void setButtonsActive(boolean active);

    void setTooltip(List<OrderedText> tooltip);

    void onSelected(T selected);
}
