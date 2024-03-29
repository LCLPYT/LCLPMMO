package work.lclpnet.mmo.data.character;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;
import work.lclpnet.lclpnetwork.model.JsonSerializable;
import work.lclpnet.mmo.data.race.MMORace;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class MMOCharacter extends JsonSerializable implements Comparable<MMOCharacter> {

    @Expose(serialize = false)
    public Integer id = null;
    @Expose(serialize = false)
    public Integer owner = null;
    protected transient String unlocalizedName;
    @Expose
    protected final String name;
    @Expose
    protected final MMORace race;
    @Expose
    private final DynamicCharacterData data;

    public MMOCharacter(String name, MMORace race, DynamicCharacterData data) {
        this.name = Objects.requireNonNull(name); // maybe add CharMatcher.ascii().matchesAllOf(name);
        generateUnlocalizedName();
        this.race = Objects.requireNonNull(race);
        this.data = Optional.ofNullable(data).orElse(DynamicCharacterData.empty());
    }

    protected void generateUnlocalizedName() {
        if (this.unlocalizedName == null)
            this.unlocalizedName = this.name.toLowerCase(Locale.ROOT).replace(' ', '_');
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
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
    public int compareTo(@NotNull MMOCharacter o) {
        // order by id, if available
        if (this.id == null || o.id == null) return this.hashCode() - o.hashCode();

        return this.id - o.id;
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
        public MMOCharacter read(JsonObject json) {
            String name = json.get("name").getAsString();
            MMORace race = MMORace.Adapter.INSTANCE.fromJsonObject(json.getAsJsonObject("race"));
            DynamicCharacterData data = null;
            if (json.has("data")) {
                JsonElement e = json.get("data");
                if (e == null || e.isJsonNull()) data = DynamicCharacterData.empty();
                else {
                    String base64 = e.getAsString();
                    if (base64 == null || base64.isEmpty()) data = DynamicCharacterData.empty();
                    else data = DynamicCharacterData.decodeFromString(base64, DynamicCharacterData.class);
                }
            }
            if (data == null) data = DynamicCharacterData.empty();

            MMOCharacter character = new MMOCharacter(name, race, data);

            if (json.has("id")) character.id = json.get("id").getAsInt();
            if (json.has("owner")) character.owner = json.get("owner").getAsInt();

            character.generateUnlocalizedName();
            return character;
        }
    }

    public static class List extends ArrayList<MMOCharacter> {
    }
}
