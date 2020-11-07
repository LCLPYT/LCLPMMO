package work.lclpnet.mmo.facade;

import static work.lclpnet.mmo.util.json.MMOGson.gson;

import com.google.gson.JsonElement;

public class JsonSerializeable {

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
