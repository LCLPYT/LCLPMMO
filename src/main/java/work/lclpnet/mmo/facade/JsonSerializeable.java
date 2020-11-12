package work.lclpnet.mmo.facade;

import static work.lclpnet.mmo.util.json.MMOGson.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import work.lclpnet.mmo.util.DebugState;
import work.lclpnet.mmo.util.json.MMOGson;
import work.lclpnet.mmo.util.json.NoSerialization;

public class JsonSerializeable {

	@Override
	public String toString() {
		return stringify(this);
	}
	
	/**
	 * Returns a non dist specific string representation of this object.
	 * Works similar to {@link #toString()}, but this method ignores every {@link NoSerialization} annotation.
	 * 
	 * @param useAdapters Use true if the type adapters from {@link MMOGson} should be invoked.
	 * @return A string representing the object.
	 */
	public String toDebugString(boolean useAdapters) {
		if(useAdapters) {
			DebugState.setDebug(true);
			String json = MMOGson.allSerializerGson.toJson(this);
			DebugState.setDebug(false);
			return json;
		} else return new Gson().toJson(this);
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
