package work.lclpnet.mmo.util.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.function.Consumer;

public class AuthManager {

	private static final String LS5_IDENTIFICATION = "$2y$10$PuKeoJ/jUBVy9fBYhr3x3egoyyA7N84zjVlnFr9q0fPjUkf8gOH.6";
	private boolean requestInProgress = false;

	public void login(String user, String password, Consumer<Boolean> callback) {
		Objects.requireNonNull(user);
		Objects.requireNonNull(password);
		Objects.requireNonNull(callback);

		if(requestInProgress) return;

		requestInProgress = true;

		JsonObject body = new JsonObject();
		body.addProperty("email", user);
		body.addProperty("password", password);
		body.addProperty("identification", LS5_IDENTIFICATION);

		LCLPNetwork.post("api/auth/ls5/access-token", body, resp -> {
			requestInProgress = false;

			if(resp.isNoConnection()) {
				callback.accept(null);
				return;
			}

			if(resp.getResponseCode() != 200) callback.accept(false);
			else {
				JsonObject obj = new Gson().fromJson(resp.getRawResponse(), JsonObject.class);
				JsonElement elem = obj.get("accessToken");

				if(elem == null) callback.accept(false);
				else AccessTokenStorage.store(elem.getAsString(), callback);
			}
		});
	}

	public void checkEmailVerified(Consumer<Boolean> callback) {
		if(requestInProgress) return;
		requestInProgress = true;

		LCLPNetwork.sendRequest("api/auth/verified", "GET", null, resp -> {
			requestInProgress = false;

			if(resp.isNoConnection()) callback.accept(null);
			else if(resp.getResponseCode() != 200) callback.accept(false);
			else {
				JsonObject obj = new Gson().fromJson(resp.getRawResponse(), JsonObject.class);
				JsonElement elem = obj.get("email_verified");

				if(elem == null) callback.accept(false);
				else callback.accept(elem.getAsBoolean());
			}
		});
	}

	public void register(String user, String password, String confirmPassword, Consumer<String> callback) {
		// TODO make screen for email verification
		throw new IllegalStateException("Temporarily disabled.");
		/*Objects.requireNonNull(user);
		Objects.requireNonNull(password);
		Objects.requireNonNull(confirmPassword);
		Objects.requireNonNull(callback);

		if(requestInProgress) return;

		requestInProgress = true;

		JsonObject body = new JsonObject();
		body.addProperty("name", user.split("@")[0]);
		body.addProperty("email", user);
		body.addProperty("password", password);
		body.addProperty("password_confirmation", confirmPassword);

		LCLPNetwork.post("api/auth/register", body, resp -> {
			requestInProgress = false;

			if(resp.isNoConnection()) {
				callback.accept(I18n.format("mmo.no_internet"));
				return;
			}

			if(resp.getResponseCode() != 201) {
				JsonObject obj = new Gson().fromJson(resp.getRawError(), JsonObject.class);
				JsonElement errors = obj.get("errors");
				String clientError = "Client error.";
				if(errors == null) {
					callback.accept(clientError);
					return;
				}
				JsonObject errObj = errors.getAsJsonObject();
				Set<Map.Entry<String, JsonElement>> entrySet = errObj.entrySet();
				if(entrySet == null) {
					callback.accept(clientError);
					return;
				}
				JsonElement errField = entrySet.iterator().next().getValue();
				if(!errField.isJsonArray()) {
					callback.accept(clientError);
					return;
				}
				JsonArray errArr = errField.getAsJsonArray();
				if(errArr.size() <= 0) {
					callback.accept(clientError);
					return;
				}
				callback.accept(errArr.get(0).getAsString());
			} else {
				login(user, password, success -> callback.accept(success ? null : "Login issue."));
			}
		});*/
	}

}
