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
    private final String rawResponse, rawError;

    public HTTPResponse(int responseCode, String rawResponse, String rawError) {
        this.responseCode = responseCode;
        this.rawResponse = rawResponse;
        this.rawError = rawError;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public String getRawError() {
        return rawError;
    }

    public static HTTPResponse fromRequest(HttpURLConnection conn) throws IOException {
        int status = conn.getResponseCode();

        String response;
        try (InputStream in = conn.getInputStream()) {
            response = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            response = null;
        }

        String error;
        try(InputStream inErr = conn.getErrorStream()) {
            error = IOUtils.toString(inErr, StandardCharsets.UTF_8);
        } catch (IOException | NullPointerException e) {
            error = null;
        }

        return new HTTPResponse(status, response, error);
    }

}
