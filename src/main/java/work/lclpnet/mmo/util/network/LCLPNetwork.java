package work.lclpnet.mmo.util.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIAuthAccess;
import work.lclpnet.lclpnetwork.facade.User;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.facade.character.MMOCharacter;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class LCLPNetwork {

    private static final Logger LOGGER = LogManager.getLogger();
    private static MMOAPI API = null;
    private static MMOCharacter selectedCharacter;
    private static User user;

    public static MMOAPI getAPI() {
        return API;
    }

    public static CompletableFuture<Void> setAccessToken(@Nullable String accessToken) {
        if (accessToken == null) {
            API = null;
            return CompletableFuture.completedFuture(null);
        }
        APIAuthAccess authAccess = new APIAuthAccess(accessToken);
        authAccess.setHost(Config.getEffectiveHost());

        return APIAccess.withAuthCheck(authAccess)
                .thenAccept(access -> API = new MMOAPI(access));
    }

    public static CompletableFuture<Boolean> logout() {
        return LCLPNetwork.API.revokeCurrentToken().handle((result, err) -> {
           // unload without considering the result
            AccessTokenStorage.store(null);
            LCLPNetwork.setUser(null);
            LCLPNetwork.setSelectedCharacter(null);
            loadActiveCharacter(null);

            if (err != null) return false;
            else return result;
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isLoggedIn() {
        return user != null;
    }

    public static User getUser() {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        return user;
    }

    public static MMOCharacter getSelectedCharacter() {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        return selectedCharacter;
    }

    public static void setUser(User user) {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        LCLPNetwork.user = user;
    }

    public static void setSelectedCharacter(MMOCharacter selectedCharacter) {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        LCLPNetwork.selectedCharacter = selectedCharacter;
    }

    public static CompletableFuture<Void> setup() {
        return AccessTokenStorage.load()
                .thenCompose(result -> LCLPNetwork.API.getCurrentUser())
                .thenAccept(user -> {
                    if (FMLEnvironment.dist == Dist.CLIENT) LCLPNetwork.loadActiveCharacter(user);
                });
    }

    @OnlyIn(Dist.CLIENT)
    public static CompletableFuture<Void> loadActiveCharacter() {
        return loadActiveCharacter(LCLPNetwork.user);
    }

    @OnlyIn(Dist.CLIENT)
    public static CompletableFuture<Void> loadActiveCharacter(@Nullable User user) {
        if (user == null) return CompletableFuture.completedFuture(null);

        LCLPNetwork.user = user;

        return MMOAPI.PUBLIC.getActiveCharacterByUserId(user.getId(), true)
                .thenAccept(character -> LCLPNetwork.selectedCharacter = character);
    }
}
