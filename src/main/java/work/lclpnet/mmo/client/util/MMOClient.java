package work.lclpnet.mmo.client.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import work.lclpnet.lclpnetwork.model.User;
import work.lclpnet.mmo.data.character.MMOCharacter;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;
import work.lclpnet.mmo.network.backend.MMOAPI;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class MMOClient {

    private static MMOCharacter character;

    public static CompletableFuture<MMOCharacter> loadActiveCharacter() {
        User user = LCLPNetworkSession.getUser();
        if (user == null) return CompletableFuture.completedFuture(null);

        return MMOAPI.PUBLIC.getActiveCharacterByUserId(user.getId(), true)
                .thenApply(character -> MMOClient.character = character);
    }
}
