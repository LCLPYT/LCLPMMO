package work.lclpnet.mmo.facade.race;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonWriter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.entity.IEntitySizeOverride;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.gui.MMOSelectionItem;
import work.lclpnet.mmo.util.json.EasyTypeAdapter;

import java.io.IOException;

public class MMORace extends JsonSerializable implements MMOSelectionItem, IEntitySizeOverride {

    @Expose
    private final String unlocalizedName;
    private final transient ITextComponent title;

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

    public static MMORace getRaceFromPlayer(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return null;

        PlayerEntity player = (PlayerEntity) entity;
        MMOCharacter character = IMMOUser.getMMOUser(player).getMMOCharacter();
        if (character == null) return null;
        return character.getRace();
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
        public MMORace read(JsonObject json) {
            return fromJsonObject(json);
        }

        public MMORace fromJsonObject(JsonObject json) {
            String unlocalizedName = json.get("unlocalizedName").getAsString();
            return Races.getByName(unlocalizedName);
        }
    }
}
