package work.lclpnet.mmo.network.backend;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.lclpnetwork.LCLPNetworkAPI;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.model.User;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.client.MMOClient;
import work.lclpnet.mmo.network.AccessTokenLoader;
import work.lclpnet.mmocontent.util.Env;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

public class LCLPNetworkSession {

    private static final Logger logger = LogManager.getLogger();
    private static MMOAPI authorizedApi = null;
    private static User user;

    public static MMOAPI getAuthorizedApi() {
        return authorizedApi;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        LCLPNetworkSession.user = user;
    }

    public static CompletableFuture<Void> startup() {
        return AccessTokenLoader.load()
                .thenCompose(LCLPNetworkSession::init)
                .exceptionally(err -> {
                    if (err.getCause() instanceof FileNotFoundException) return null;
                    else logger.error("Could not startup LCLPNetwork session", err);

                    return null;
                });
    }

    private static CompletableFuture<MMOAPI> createApiConnection(String accessToken) {
        APIAuthAccess authAccess = new APIAuthAccess(accessToken);
        authAccess.setHost(Config.getEffectiveHost());

        return APIAccess.withAuthCheck(authAccess)
                .thenApply(access -> authorizedApi = new MMOAPI(access));
    }

    public static CompletableFuture<Void> init(@Nullable String accessToken) {
        if (accessToken == null) {
            authorizedApi = null;
            return CompletableFuture.completedFuture(null);
        }

        return createApiConnection(accessToken)
                .thenCompose(LCLPNetworkAPI::getCurrentUser)
                .thenCompose(user -> {
                    if (user == null) throw new IllegalStateException("Invalid response");

                    LCLPNetworkSession.user = user;
                    logger.info("Logged into LCLPNetwork as {}#{}.", user.getName(), user.getId());

                    return Env.isClient() ? startupClient() : CompletableFuture.completedFuture(null);
                })
                .exceptionally(err -> {
                    logger.warn("Could not log into LCLPNetwork", err);
                    err.printStackTrace();
                    return null;
                });
    }

    @Environment(EnvType.CLIENT)
    public static CompletableFuture<Void> startupClient() {
        if (user == null) throw new IllegalStateException("User not initialized.");
        return MMOClient.loadActiveCharacter()
                .thenAccept(MMOClient::logActiveCharacterLoaded)
                .exceptionally(err -> {
                    logger.error("Failed to fetch active character", err);
                    return null;
                });
    }

    public static boolean isLoggedIn() {
        return user != null;
    }
}
