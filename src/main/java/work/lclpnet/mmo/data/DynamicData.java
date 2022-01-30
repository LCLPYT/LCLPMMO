package work.lclpnet.mmo.data;

import work.lclpnet.lclpnetwork.model.JsonSerializable;
import work.lclpnet.mmo.util.MMOUtils;
import work.lclpnet.mmo.util.json.MMOGson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DynamicData extends JsonSerializable {

    public String encryptToString() {
        String json = this.toString();

        try (ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MMOUtils.Data.encodeGzBase64(in, out);
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends DynamicData> T decodeFromString(String s, Class<T> clazz) {
        String json;
        try (ByteArrayInputStream in = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            MMOUtils.Data.decodeGzBase64(in, out);
            json = new String(out.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parse(json, clazz, MMOGson.gson);
    }
}
