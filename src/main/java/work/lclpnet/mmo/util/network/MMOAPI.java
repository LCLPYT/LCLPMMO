package work.lclpnet.mmo.util.network;

import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;
import work.lclpnet.lclpnetwork.util.JsonBuilder;
import work.lclpnet.mmo.facade.character.MMOCharacter;

import java.util.concurrent.CompletableFuture;

public class MMOAPI extends LCLPMinecraftAPI {

    public static final MMOAPI PUBLIC = new MMOAPI(APIAccess.PUBLIC);

    /**
     * Construct a new MMOAPI object.
     *
     * @param access The API accessor to use.
     */
    public MMOAPI(APIAccess access) {
        super(access);
    }

    public CompletableFuture<MMOCharacter> getActiveCharacterByUserId(long userId, boolean fetchData) {
        return api.post("api/ls5/get-active-character", JsonBuilder.object()
                .set("userId", userId)
                .set("fetchData", fetchData)
                .createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            else return resp.getExtra(MMOCharacter.class);
        });
    }

    public CompletableFuture<MMOCharacter> getActiveCharacterByUuid(String uuid) {
        return api.post("api/ls5/get-active-character-by-uuid", JsonBuilder.object()
                .set("uuid", uuid)
                .createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            else return resp.getExtra(MMOCharacter.class);
        });
    }
}
