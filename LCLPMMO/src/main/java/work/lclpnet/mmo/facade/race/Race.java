package work.lclpnet.mmo.facade.race;

import com.google.gson.Gson;

import net.minecraft.util.text.ITextComponent;
import work.lclpnet.mmo.gui.racechooser.MMOMenuItem;

public class Race implements MMOMenuItem{

	private String name;
	private ITextComponent title;
	
	public Race(String name, ITextComponent title) {
		this.name = name;
		this.title = title;
	}
	
	public String getName() {
		return name;
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
