package work.lclpnet.mmo.gui;

import java.util.List;

import net.minecraft.util.IReorderingProcessor;

public interface GenericSelectionSetup<T extends MMOSelectionItem> {

	void setButtonsActive(boolean active);
	
	void setTooltip(List<IReorderingProcessor> tooltip);
	
	void onSelected(T selected);
	
}
