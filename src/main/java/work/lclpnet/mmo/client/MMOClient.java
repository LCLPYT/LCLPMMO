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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MMOClient {

    private static final Logger logger = LogManager.getLogger();
    private static MMOCharacter activeCharacter;
    private static List<MMOCharacter> cachedCharacters = new ArrayList<>();

    public static void setActiveCharacter(MMOCharacter activeCharacter) {
        MMOClient.activeCharacter = activeCharacter;
    }

    public static MMOCharacter getActiveCharacter() {
        return activeCharacter;
    }

    public static void setCachedCharacters(List<MMOCharacter> characters) {
        cachedCharacters = Objects.requireNonNull(characters);
    }

    public static List<MMOCharacter> getCachedCharacters() {
        return cachedCharacters;
    }

    public static CompletableFuture<MMOCharacter> loadActiveCharacter() {
        User user = LCLPNetworkSession.getUser();
        if (user == null) return CompletableFuture.completedFuture(null);

        return MMOAPI.PUBLIC.getActiveCharacterByUserId(user.getId(), true)
                .thenApply(character -> {
                    setActiveCharacter(character);
                    return character;
                });
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

    public static CompletableFuture<List<MMOCharacter>> fetchAndCacheCharacters(boolean updateActiveCharacter) {
        return LCLPNetworkSession.getAuthorizedApi().getCharacters().thenCompose(characters -> {
            MMOClient.setCachedCharacters(characters);

            if (MMOClient.getActiveCharacter() == null || updateActiveCharacter) {
                return MMOClient.loadActiveCharacter()
                        .thenApply(active -> characters)
                        .exceptionally(err -> {
                            err.printStackTrace();
                            return characters;
                        });
            } else {
                return CompletableFuture.completedFuture(cachedCharacters);
            }
        }).exceptionally(err -> new ArrayList<>());
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

    public static void logActiveCharacterLoaded(@Nullable MMOCharacter character) {
        if (character == null) logger.info("No active character was loaded.");
        else logger.info("Loaded active character {}#{}", character.getName(), character.id);
    }
}
