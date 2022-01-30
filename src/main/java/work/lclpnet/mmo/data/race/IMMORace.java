package work.lclpnet.mmo.data.race;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import work.lclpnet.lclpnetwork.model.JsonSerializable;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.client.gui.select.IMMOSelectionItem;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;

import java.io.IOException;

public class IMMORace extends JsonSerializable implements IMMOSelectionItem {

    @Expose
    private final String unlocalizedName;
    private final transient Text title;

    public IMMORace(String unlocalizedName, Text title) {
        this.unlocalizedName = unlocalizedName;
        this.title = title;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public Text getTitle() {
        return title;
    }

    public static IMMORace of(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return null;

        PlayerEntity player = (PlayerEntity) entity;
        MMOCharacter character = IMMOUser.of(player).getMMOCharacter();
        return character != null ? character.getRace() : null;
    }

    public static class Adapter extends EasyTypeAdapter<IMMORace> {

        public static final Adapter INSTANCE = new Adapter();

        protected Adapter() {
            super(IMMORace.class);
        }

        @Override
        public void write(JsonWriter out, IMMORace value) throws IOException {
            out.beginObject();

            addField("unlocalizedName", out, w -> w.value(value.unlocalizedName));

            out.endObject();
        }

        @Override
        public IMMORace read(JsonObject json) {
            return fromJsonObject(json);
        }

        public IMMORace fromJsonObject(JsonObject json) {
            String unlocalizedName = json.get("unlocalizedName").getAsString();
            return Races.getByName(unlocalizedName);
        }
    }
}
