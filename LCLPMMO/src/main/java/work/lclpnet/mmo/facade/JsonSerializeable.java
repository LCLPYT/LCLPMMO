package work.lclpnet.mmo.facade;

import java.io.IOException;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.facade.quest.Quest;
import work.lclpnet.mmo.facade.quest.Quests;
import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;
import work.lclpnet.mmo.util.NoSerialization;

public class JsonSerializeable {

	private static Gson gson;

	static {
		GsonBuilder builder = new GsonBuilder();
		
		builder.addSerializationExclusionStrategy(new ExclusionStrategy() {
			
			@Override
			public boolean shouldSkipField(FieldAttributes f) {
				NoSerialization annotation = f.getAnnotation(NoSerialization.class);
				if(annotation == null) return false;
				
				return annotation.in().isApplicable();
			}
			
			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return false;
			}
		});
		
		JsonDeserializer<MMORace> raceAdapter = (JsonDeserializer<MMORace>) (json, typeOfT, context) -> {
			JsonObject obj = json.getAsJsonObject();
			String s = obj.get("unlocalizedName").getAsString();

			MMORace r = Races.getByName(s);
			if(r == null) throw new IllegalStateException("Race '" + s + "' not registered!");

			return r;
		};
		builder.registerTypeAdapter(MMORace.class, raceAdapter);
		
		final Gson characterGson = new GsonBuilder().registerTypeAdapter(MMORace.class, raceAdapter).create();
		JsonDeserializer<MMOCharacter> characterAdapter = (JsonDeserializer<MMOCharacter>) (json, typeOfT, context) -> {
			MMOCharacter c = characterGson.fromJson(json, MMOCharacter.class);
			c.generateUnlocalizedName();
			return c;
		};
		builder.registerTypeAdapter(MMOCharacter.class, characterAdapter);
		
		builder.registerTypeAdapter(Quest.class, new TypeAdapter<Quest>() {

			@Override
			public void write(JsonWriter out, Quest value) throws IOException {
				out.value(value == null ? null : value.getIdentifier());
			}

			@Override
			public Quest read(JsonReader in) throws IOException {
				String ident = in.nextString();
				return Quests.getQuestByIdentifier(ident);
			}
		});
		
		gson = builder.create();
	}
	
	@Override
	public String toString() {
		return stringify(this);
	}

	public JsonElement toJson() {
		return toJson(this);
	}

	public static JsonElement toJson(Object o) {
		return gson.toJsonTree(o);
	}

	public static String stringify(Object o) {
		return gson.toJson(o);
	}

	public static <T> T parse(String s, Class<T> clazz) {
		return gson.fromJson(s, clazz);
	}

	public static <T> T cast(JsonElement elem, Class<T> clazz) {
		return gson.fromJson(elem, clazz);
	}

}
