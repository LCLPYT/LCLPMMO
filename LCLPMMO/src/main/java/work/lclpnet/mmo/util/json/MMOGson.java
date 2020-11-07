package work.lclpnet.mmo.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.race.MMORace;

public class MMOGson {

	public static final Gson gson = new GsonBuilder()
			.addSerializationExclusionStrategy(new NoSerialization.Strategy())
			.registerTypeAdapter(MMORace.class, MMORace.Adapter.INSTANCE)
			.registerTypeAdapter(MMOCharacter.class, new MMOCharacter.Adapter())
			.create();

}
