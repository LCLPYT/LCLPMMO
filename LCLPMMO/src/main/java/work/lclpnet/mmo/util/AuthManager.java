package work.lclpnet.mmo.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
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

            LCLPNetwork.sendRequest("api/auth/issue-token", "POST", body, resp -> {
                requestInProgress = false;

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

}
