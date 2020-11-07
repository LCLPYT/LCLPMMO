package work.lclpnet.mmo.facade;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import work.lclpnet.mmo.util.DataUtils;

public class DynamicData extends JsonSerializeable {

	public String encryptToString() {
		String json = this.toString();
		
		try (ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
				ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			DataUtils.uncompressedToGzBase64(in, out);
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
			DataUtils.gzBase64ToUncompressed(in, out);
			json = new String(out.toByteArray(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return parse(json, clazz);
	}
	
}
