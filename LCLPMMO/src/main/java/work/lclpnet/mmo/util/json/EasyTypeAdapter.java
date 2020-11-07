package work.lclpnet.mmo.util.json;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

public abstract class EasyTypeAdapter<T> extends TypeAdapter<T> {

	protected abstract T read(JsonObject json) throws IOException;
	
	@Override
	public T read(JsonReader in) throws IOException {
		JsonElement jsonElement = Streams.parse(in);
		if(!jsonElement.isJsonObject()) throw new JsonParseException("The JsonElement read is not a JsonObject!");
		
		return read(jsonElement.getAsJsonObject());
	}
	
}
