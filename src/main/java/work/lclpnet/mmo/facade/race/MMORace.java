package work.lclpnet.mmo.facade.race;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.text.ITextComponent;
import work.lclpnet.mmo.entity.IEntitySizeOverride;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.gui.MMOSelectionItem;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;

public class MMORace extends JsonSerializeable implements MMOSelectionItem, IEntitySizeOverride {

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

	public static class Adapter extends EasyTypeAdapter<MMORace> {

		public static final Adapter INSTANCE = new Adapter();

		protected Adapter() {
			super(MMORace.class);
		}

		@Override
		public void write(JsonWriter out, MMORace value) throws IOException {
			out.beginObject();

			addField("unlocalizedName", out, w -> w.value(value.unlocalizedName));

			out.endObject();
		}

		@Override
		public MMORace read(JsonObject json) throws IOException {
			return fromJsonObject(json);
		}

		public MMORace fromJsonObject(JsonObject json) {
			String unlocalizedName = json.get("unlocalizedName").getAsString();
			return Races.getByName(unlocalizedName);
		}

	}

}
