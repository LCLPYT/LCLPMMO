package work.lclpnet.mmo.util.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import work.lclpnet.mmo.util.fun.IOConsumer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class EasyTypeAdapter<T> extends TypeAdapter<T> {

    protected final Class<T> clazz;

    public EasyTypeAdapter(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract T read(JsonObject json);

    @Override
    public T read(JsonReader in) throws IOException {
        JsonElement jsonElement = Streams.parse(in);
        if (!jsonElement.isJsonObject()) throw new JsonParseException("The JsonElement read is not a JsonObject!");

        return read(jsonElement.getAsJsonObject());
    }

    /**
     * Adds a field, when {@link #outputAllowed(String)}.
     *
     * @param name        The name of the field to add.
     * @param whenAllowed A consumer, that is only called when the field is added. Set the field value here.
     * @return true, if the field was added.
     */
    public boolean addField(String name, JsonWriter out, IOConsumer<JsonWriter> whenAllowed) throws IOException {
        if (outputAllowed(name)) {
            out.name(name);
            whenAllowed.accept(out);
            return true;
        }
        return false;
    }

    public boolean outputAllowed(String fieldName) {
        try {
            return outputAllowed(clazz.getDeclaredField(fieldName));
        } catch (NoSuchFieldException | SecurityException e) {
            IllegalStateException ex = new IllegalStateException("Could not fetch field.");
            ex.addSuppressed(e);
            throw ex;
        }
    }

    public static boolean outputAllowed(Field f) {
        return !Modifier.isTransient(f.getModifiers());
    }
}
