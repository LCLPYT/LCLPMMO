package work.lclpnet.mmo.facade.character;

import java.util.Locale;
import java.util.Objects;

import com.google.gson.Gson;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.gui.MMOSelectionItem;

public class MMOCharacter implements MMOSelectionItem{

	protected final String name, unlocalizedName;
	protected MMORace race;
	
	public MMOCharacter(String name, MMORace race) {
		this.name = Objects.requireNonNull(name);
		this.unlocalizedName = this.name.toLowerCase(Locale.ROOT).replace(' ', '_');
		this.race = Objects.requireNonNull(race);
	}
	
	public String getName() {
		return name;
	}
	
	public MMORace getRace() {
		return race;
	}
	
	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	@Override
	public ITextComponent getTitle() {
		return new StringTextComponent(name);
	}
	
	@Override
	public String getUnlocalizedName() {
		return unlocalizedName;
	}
	
	@Override
	public String getFirstLine() {
		return new TranslationTextComponent("mmo.menu.select_character.entry_desc", this.race.getTitle().getFormattedText()).getFormattedText();
	}
	
	@Override
	public ResourceLocation getIcon() {
		return this.race.getIcon();
	}

}
