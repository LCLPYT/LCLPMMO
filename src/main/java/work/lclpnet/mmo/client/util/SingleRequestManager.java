package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import work.lclpnet.mmo.network.backend.MMOAPI;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Environment(EnvType.CLIENT)
public class SingleRequestManager {

    private boolean locked = false;

    public CompletableFuture<Boolean> login(String user, String password) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);

        if (locked) return CompletableFuture.completedFuture(null);
        locked = true;

        return MMOAPI.PUBLIC.getAccessToken(user, password).thenCompose(token -> {
            if (token == null) return CompletableFuture.completedFuture(null);

            return ClientAccessTokenSaver.store(token).handle((result, err) -> {
                if (err != null) err.printStackTrace(); // Saving the token failed, but the token is still valid
                return true;
            });
        }).whenComplete((result, err) -> locked = false);
    }
}
