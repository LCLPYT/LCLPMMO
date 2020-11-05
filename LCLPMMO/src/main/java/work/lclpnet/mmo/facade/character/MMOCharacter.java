package work.lclpnet.mmo.facade.character;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import work.lclpnet.mmo.facade.NetworkWriteable;
import work.lclpnet.mmo.facade.quest.QuestBook;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.gui.MMOSelectionItem;
import work.lclpnet.mmo.util.DistSpecifier;
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
	@NoSerialization(in = DistSpecifier.CLIENT)
	public QuestBook questBook = null; // TODO make protected when debug finished
	
	public MMOCharacter(String name, MMORace race, QuestBook questBook) {
		this.name = Objects.requireNonNull(name); // maybe add CharMatcher.ascii().matchesAllOf(name);
		generateUnlocalizedName();
		this.race = Objects.requireNonNull(race);
		this.questBook = Optional.ofNullable(questBook).orElse(new QuestBook());
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
	
	public QuestBook getQuestBook() {
		return questBook;
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
		return I18n.format("mmo.menu.select_character.entry_desc", this.race.getTitle().getString());
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
