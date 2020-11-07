package work.lclpnet.mmo.facade.character;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import work.lclpnet.mmo.facade.NetworkWriteable;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.gui.MMOSelectionItem;
import work.lclpnet.mmo.util.DistSpecifier;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;
import work.lclpnet.mmo.util.json.NoSerialization;
import work.lclpnet.mmo.util.network.LCLPNetwork;

public class MMOCharacter extends NetworkWriteable implements MMOSelectionItem {

	@NoSerialization
	public Integer id = null;
	@NoSerialization
	public Integer owner = null;
	protected transient String unlocalizedName;
	protected final String name;
	protected final MMORace race;
	@NoSerialization(in = DistSpecifier.CLIENT)
	private final DynamicCharacterData data;

	public MMOCharacter(String name, MMORace race, DynamicCharacterData data) {
		this.name = Objects.requireNonNull(name); // maybe add CharMatcher.ascii().matchesAllOf(name);
		generateUnlocalizedName();
		this.race = Objects.requireNonNull(race);
		this.data = Optional.ofNullable(data).orElse(DynamicCharacterData.empty());
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

	public DynamicCharacterData getData() {
		return data;
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

	public static class Adapter extends EasyTypeAdapter<MMOCharacter> {

		public Adapter() {
			super(MMOCharacter.class);
		}

		@Override
		public void write(JsonWriter out, MMOCharacter value) throws IOException {
			out.beginObject();

			addField("id", out, w -> w.value(value.id));
			addField("owner", out, w -> w.value(value.owner));
			addField("name", out, w -> w.value(value.name));
			addField("unlocalizedName", out, w -> w.value(value.unlocalizedName)); // DEBUG ONLY
			addField("race", out, w -> MMORace.Adapter.INSTANCE.write(w, value.race));
			addField("data", out, w -> w.value(value.data.encryptToString()));
			
			out.endObject();
		}

		@Override
		public MMOCharacter read(JsonObject json) throws IOException {
			String name = json.get("name").getAsString();
			MMORace race = MMORace.Adapter.INSTANCE.fromJsonObject(json.getAsJsonObject("race"));
			DynamicCharacterData data = null;
			if(json.has("data")) {
				String base64 = json.get("data").getAsString();
				data = DynamicCharacterData.decodeFromString(base64, DynamicCharacterData.class);
			}
			if(data == null) data = DynamicCharacterData.empty();

			MMOCharacter character = new MMOCharacter(name, race, data);

			if(json.has("id")) character.id = json.get("id").getAsInt();
			if(json.has("owner")) character.owner = json.get("owner").getAsInt();

			character.generateUnlocalizedName();
			return character;
		}

	}

}
