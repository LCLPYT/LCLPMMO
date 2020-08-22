package work.lclpnet.mmo.facade.race;

import com.google.gson.Gson;

import net.minecraft.util.text.ITextComponent;
import work.lclpnet.mmo.gui.MMOSelectionItem;

public class MMORace implements MMOSelectionItem{

	private String unlocalizedName;
	private ITextComponent title;
	
	public MMORace(String unlocalizedName, ITextComponent title) {
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
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
}
