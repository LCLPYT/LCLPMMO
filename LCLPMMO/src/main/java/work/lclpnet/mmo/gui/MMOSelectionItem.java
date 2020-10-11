package work.lclpnet.mmo.gui;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface MMOSelectionItem {

	ITextComponent getTitle();
	
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
