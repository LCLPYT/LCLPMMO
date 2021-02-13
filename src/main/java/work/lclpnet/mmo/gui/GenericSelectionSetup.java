package work.lclpnet.mmo.gui;

import net.minecraft.util.IReorderingProcessor;

import java.util.List;

public interface GenericSelectionSetup<T extends MMOSelectionItem> {

	void setButtonsActive(boolean active);
	
	void setTooltip(List<IReorderingProcessor> tooltip);
	
	void onSelected(T selected);
	
}
