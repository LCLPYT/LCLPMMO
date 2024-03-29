package work.lclpnet.mmo.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.data.quest.Quest;
import work.lclpnet.mmo.data.race.MMORace;

public class MMOGson {

    public static final Gson gson = builder()
            .create();

    private static GsonBuilder builder() {
        return new GsonBuilder()
                .registerTypeAdapter(MMORace.class, MMORace.Adapter.INSTANCE)
                .registerTypeAdapter(MMOCharacter.class, new MMOCharacter.Adapter())
                .registerTypeAdapter(Quest.class, new Quest.Adapter());
    }
}
