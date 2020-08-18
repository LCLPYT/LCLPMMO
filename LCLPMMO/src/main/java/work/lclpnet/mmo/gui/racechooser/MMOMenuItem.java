package work.lclpnet.mmo.gui.racechooser;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface MMOMenuItem {

	ITextComponent getTitle();
	
	default ResourceLocation getIcon() {
		return null;
	}
	
	default String getFirstLine() {
		return null;
	}
	
	default String getSecondLine() {
		return null;
	}
	
	default String getToolTip() {
		return null;
	}
	
}
