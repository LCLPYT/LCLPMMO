package work.lclpnet.mmo.util;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import work.lclpnet.mmo.facade.JsonSerializeable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HTTPResponse {

    public static final HTTPResponse NO_CONNECTION = new HTTPResponse(-1, null, null);
    private final int responseCode;
    private final String rawResponse, rawError;
    private ValidationViolations validationViolations = null;

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

    public boolean isNoConnection() {
        return NO_CONNECTION.equals(this);
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
    
    public boolean hasValidationViolations() {
    	return getValidationViolations() != null && !validationViolations.getViolations().isEmpty();
    }
    
    public ValidationViolations getValidationViolations() {
    	if(validationViolations != null) return validationViolations;
    	
    	if(rawError == null) return null;
    	
    	JsonObject json;
    	try {
    		json = JsonSerializeable.parse(rawError, JsonObject.class);
    	} catch (JsonSyntaxException e) {
    		return null;
		}
    	
    	JsonElement elem = json.get("errors");
    	if(elem == null || !elem.isJsonObject()) return null;

    	List<ElementError> elemErrors = new ArrayList<>();

    	JsonObject obj = elem.getAsJsonObject();
    	obj.entrySet().forEach(e -> {
    		List<String> errors = new ArrayList<>();
    		if(e.getValue().isJsonArray()) {
    			List<JsonElement> elems = new ArrayList<>();
    			e.getValue().getAsJsonArray().forEach(elems::add);
    			elems.forEach(eElem -> errors.add(eElem.toString()));
    		}
    		elemErrors.add(new ElementError(e.getKey(), errors));	
    	});
    	
    	return (validationViolations = new ValidationViolations(elemErrors));
    }
    
    @Override
    public String toString() {
        return "HTTPResponse{" +
                "responseCode=" + responseCode +
                ", rawResponse='" + rawResponse + '\'' +
                ", rawError='" + rawError + '\'' +
                '}';
    }
}
