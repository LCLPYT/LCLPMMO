package work.lclpnet.mmo.gui;

public interface GenericSelectionSetup<T extends MMOSelectionItem> {

	void setButtonsActive(boolean active);
	
	void setTooltip(String tooltip);
	
	void onSelected(T selected);
	
}
