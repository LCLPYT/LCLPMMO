package work.lclpnet.mmo.client;

import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.lclpnetwork.model.User;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.client.util.ClientAccessTokenSaver;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;
import work.lclpnet.mmo.network.backend.MMOAPI;

import java.util.concurrent.CompletableFuture;

public class MMOClient {

    private static final Logger logger = LogManager.getLogger();
    private static MMOCharacter activeCharacter;

    public static void setActiveCharacter(MMOCharacter activeCharacter) {
        MMOClient.activeCharacter = activeCharacter;
    }

    public static MMOCharacter getActiveCharacter() {
        return activeCharacter;
    }

    public static CompletableFuture<MMOCharacter> loadActiveCharacter() {
        User user = LCLPNetworkSession.getUser();
        if (user == null) return CompletableFuture.completedFuture(null);

        return MMOAPI.PUBLIC.getActiveCharacterByUserId(user.getId(), true)
                .thenApply(character -> MMOClient.activeCharacter = character);
    }

    public static CompletableFuture<Boolean> logout() {
        LCLPNetworkSession.setUser(null);
        activeCharacter = null;

        return ClientAccessTokenSaver.store(null).thenCompose(nil -> LCLPNetworkSession.getAuthorizedApi()
                .revokeCurrentToken().handle((result, err) -> {
                    if (err != null) return false;
                    else return result;
                })
        ).handle((result, err) -> {
            if (err != null) return false;
            else return result;
        });
    }

    /**
     * Adjusts a client player's data to match with this client's data.
     * @param player The own player. Should not be an instance of another user's player.
     */
    public static void initializeMyPlayer(ClientPlayerEntity player) {
        IMMOUser user = IMMOUser.of(player);
        user.setUser(LCLPNetworkSession.getUser());
        user.setMMOCharacter(MMOClient.getActiveCharacter());
    }

    public static void logActiveCharacterLoaded(MMOCharacter character) {
        logger.info("Loaded active character {}#{}", character.getName(), character.id);
    }
}
