package work.lclpnet.mmo.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.quest.Quest;
import work.lclpnet.mmo.facade.race.MMORace;

public class MMOGson {

	public static final Gson gson = builder()
			.addSerializationExclusionStrategy(new NoSerialization.Strategy())
			.create(),

			allSerializerGson = builder()
			.create();

	private static GsonBuilder builder() {
		return new GsonBuilder()
				.registerTypeAdapter(MMORace.class, MMORace.Adapter.INSTANCE)
				.registerTypeAdapter(MMOCharacter.class, new MMOCharacter.Adapter())
				.registerTypeAdapter(Quest.class, new Quest.Adapter());
	}

}
