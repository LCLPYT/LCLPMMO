package work.lclpnet.mmo.facade;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import work.lclpnet.mmo.facade.race.MMORace;
import work.lclpnet.mmo.facade.race.Races;

public class JsonSerializeable {

	private static Gson gson;
	
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(MMORace.class, new JsonDeserializer<MMORace>() {

			@Override
			public MMORace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject obj = json.getAsJsonObject();
				String s = obj.get("unlocalizedName").getAsString();
				
				MMORace r = Races.getByName(s);
				if(r == null) throw new IllegalStateException("Race '" + s + "' not registered!");
				
				return r;
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
