package work.lclpnet.mmo.util;

import net.minecraft.client.Minecraft;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class HTTPResponse {

    private final int responseCode;
    private final String rawResponse;

    public HTTPResponse(int responseCode, String rawResponse) {
        this.responseCode = responseCode;
        this.rawResponse = rawResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public static HTTPResponse fromRequest(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();
        String response;
        try (InputStream in = conn.getInputStream()) {
            response = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            response = null;
        }
        return new HTTPResponse(status, response);
    }

}
