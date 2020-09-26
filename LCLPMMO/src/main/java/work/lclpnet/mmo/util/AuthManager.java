package work.lclpnet.mmo.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.resources.I18n;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class AuthManager {

    private boolean requestInProgress = false;

    public void login(String user, String password, Consumer<Boolean> callback) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        Objects.requireNonNull(callback);

        if(requestInProgress) return;

        requestInProgress = true;

        new Thread(() -> {
            JsonObject body = new JsonObject();
            body.addProperty("email", user);
            body.addProperty("password", password);
            body.addProperty("remember_me", true);
            body.addProperty("app_name", "LCLPMMO Client");

            LCLPNetwork.post("api/auth/issue-token", body, resp -> {
                requestInProgress = false;

                if(resp.isNoConnection()) {
                    callback.accept(null);
                    return;
                }

                if(resp.getResponseCode() != 200) callback.accept(false);
                else {
                    JsonObject obj = new Gson().fromJson(resp.getRawResponse(), JsonObject.class);
                    JsonElement elem = obj.get("access_token");

                    if(elem == null) callback.accept(false);
                    else LCLPNetwork.setAccessToken(elem.getAsString(), callback);
                }
            });
        }, "LCLPNetwork login requester").start();
    }

    public void register(String user, String password, String confirmPassword, Consumer<String> callback) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        Objects.requireNonNull(confirmPassword);
        Objects.requireNonNull(callback);

        if(requestInProgress) return;

        requestInProgress = true;

        new Thread(() -> {
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
            });
        }, "LCLPNetwork registration requester").start();
    }

}
