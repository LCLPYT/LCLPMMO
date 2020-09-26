package work.lclpnet.mmo.facade.race;

import net.minecraft.util.text.ITextComponent;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.gui.MMOSelectionItem;

public class MMORace extends JsonSerializeable implements MMOSelectionItem{

	private final String unlocalizedName;
	private transient ITextComponent title;
	
	MMORace(String unlocalizedName, ITextComponent title) {
		this.unlocalizedName = unlocalizedName;
		this.title = title;
	}
	
	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}
	
	@Override
	public ITextComponent getTitle() {
		return title;
	}
	
}
