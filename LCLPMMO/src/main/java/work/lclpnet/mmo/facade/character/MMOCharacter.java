package work.lclpnet.mmo.facade.character;

import java.util.Locale;
import java.util.Objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import work.lclpnet.mmo.facade.NetworkWriteable;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.gui.MMOSelectionItem;
import work.lclpnet.mmo.util.LCLPNetwork;
import work.lclpnet.mmo.util.NoSerialization;

public class MMOCharacter extends NetworkWriteable implements MMOSelectionItem{

	@NoSerialization
	public Integer id = null;
	@NoSerialization 
	public Integer owner = null;
	protected transient String unlocalizedName;
	protected final String name;
	protected final MMORace race;
	
	public MMOCharacter(String name, MMORace race) {
		this.name = Objects.requireNonNull(name); // maybe add CharMatcher.ascii().matchesAllOf(name);
		generateUnlocalizedName();
		this.race = Objects.requireNonNull(race);
	}

	public void generateUnlocalizedName() {
		if(this.unlocalizedName == null) 
			this.unlocalizedName = this.name.toLowerCase(Locale.ROOT).replace(' ', '_');
	}

	public String getName() {
		return name;
	}
	
	public MMORace getRace() {
		return race;
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

	@Override
	protected String getSavePath() {
		return LCLPNetwork.BACKEND.getCharacterSavePath();
	}

}
