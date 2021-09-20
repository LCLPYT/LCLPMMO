package work.lclpnet.mmo.util.network;

import work.lclpnet.lclpnetwork.api.APIException;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public class AuthManager {

    private boolean requestInProgress = false;

    public CompletableFuture<Boolean> login(String user, String password) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);

        if (requestInProgress) return CompletableFuture.completedFuture(null);
        requestInProgress = true;

        return MMOAPI.PUBLIC.getAccessToken(user, password).thenCompose(token -> {
            if (token == null) return CompletableFuture.completedFuture(null);

            return AccessTokenStorage.store(token).handle((result, err) -> {
                if (err != null) err.printStackTrace(); // Saving the token failed, but the token is still valid
                return true;
            });
        }).whenComplete((result, err) -> requestInProgress = false);
    }

    public CompletableFuture<Boolean> checkEmailVerified() {
        if (requestInProgress) return CompletableFuture.completedFuture(null);
        requestInProgress = true;

        return LCLPNetwork.getAPI().isCurrentUserVerified().whenComplete((result, err) -> {

        }).exceptionally(completionError -> {
            Throwable err = completionError instanceof CompletionException ? completionError.getCause() : completionError;
            if (err == null) err = completionError;

            if (APIException.NO_CONNECTION.equals(err)) return null;
            else if (err instanceof ResponseEvaluationException) {
                APIResponse response = ((ResponseEvaluationException) err).getResponse();
                if (response.getResponseCode() != 200) return false;
            }
            else err.printStackTrace();
            return null;
        }).whenComplete((result, err) -> requestInProgress = false);
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
