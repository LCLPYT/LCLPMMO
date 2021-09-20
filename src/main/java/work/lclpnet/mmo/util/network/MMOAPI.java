package work.lclpnet.mmo.util.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import work.lclpnet.lclpnetwork.api.APIAccess;
import work.lclpnet.lclpnetwork.api.APIResponse;
import work.lclpnet.lclpnetwork.api.ResponseEvaluationException;
import work.lclpnet.lclpnetwork.api.annotation.AuthRequired;
import work.lclpnet.lclpnetwork.api.annotation.Scopes;
import work.lclpnet.lclpnetwork.ext.LCLPMinecraftAPI;
import work.lclpnet.lclpnetwork.facade.JsonSerializable;
import work.lclpnet.lclpnetwork.util.JsonBuilder;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.json.MMOGson;

import java.util.ArrayList;
import java.util.List;
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

    protected static <T> T getExtra(APIResponse resp, Class<T> clazz) {
        JsonObject obj = JsonSerializable.parse(resp.getRawResponse(), JsonObject.class, MMOGson.gson);
        JsonElement elem = obj.get("extra");
        if (elem == null || elem.isJsonNull()) return null;
        else return JsonSerializable.cast(elem.getAsJsonObject(), clazz, MMOGson.gson);
    }

    public CompletableFuture<String> getAccessToken(String email, String password) {
        return api.post("api/auth/ls5/access-token", JsonBuilder.object()
                .set("email", email)
                .set("password", password)
                .set("identification", "$2y$10$PuKeoJ/jUBVy9fBYhr3x3egoyyA7N84zjVlnFr9q0fPjUkf8gOH.6") // LS5 identification
                .createObject()).thenApply(resp -> {
            if (resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);

            JsonObject body = resp.getResponseAs(JsonObject.class);
            JsonElement tokenElem = body.get("accessToken");
            if (tokenElem == null) throw new ResponseEvaluationException("No access token in response.", resp);

            return tokenElem.getAsString();
        });
    }

    /**
     * Fetches the active character of a user by id.
     *
     * @param userId The id of the user.
     * @param fetchData Whether to fetch character data.
     * @return A completable future that will contain the active {@link MMOCharacter}.
     */
    public CompletableFuture<MMOCharacter> getActiveCharacterByUserId(long userId, boolean fetchData) {
        return api.post("api/ls5/get-active-character", JsonBuilder.object()
                .set("userId", userId)
                .set("fetchData", fetchData)
                .createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            else return getExtra(resp, MMOCharacter.class); // use MMOGson because of type adapters
        });
    }

    /**
     * Fetches the active character of an MC user by Minecraft UUID.
     *
     * @param uuid The Minecraft UUID of the Minecraft user.
     * @return A completable future that will contain the active {@link MMOCharacter}.
     */
    public CompletableFuture<MMOCharacter> getActiveCharacterByUuid(String uuid, boolean fetchData) {
        return api.post("api/ls5/get-active-character-by-uuid", JsonBuilder.object()
                .set("uuid", uuid)
                .set("fetchData", fetchData)
                .createObject()).thenApply(resp -> {
            if(resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            else return getExtra(resp, MMOCharacter.class);
        });
    }

    /**
     * Fetches a list of all characters of the current user.
     * @return A completable future that will contain a list of all the {@link MMOCharacter}s of a user.
     */
    @AuthRequired
    @Scopes("ls5")
    public CompletableFuture<List<MMOCharacter>> getCharacters() {
        return api.post("api/ls5/get-characters", null).thenApply(resp -> {
            if (resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            else {
                List<MMOCharacter> characters = new ArrayList<>();
                resp.getResponseAs(JsonArray.class).forEach(element -> characters.add(MMOCharacter.cast(element, MMOCharacter.class, MMOGson.gson)));
                return characters;
            }
        });
    }

    /**
     * Creates a new MMO character for the current user.
     *
     * @param name The character's name.
     * @param unlocalizedRaceName The character's race.
     * @return A completable future that will be completed, if the character was created.
     */
    @AuthRequired
    @Scopes("ls5-write")
    public CompletableFuture<Void> addCharacter(String name, String unlocalizedRaceName) {
        return api.post("api/ls5/add-character", JsonBuilder.object()
                .set("name", name)
                .beginObject("race")
                .set("unlocalizedName", unlocalizedRaceName)
                .endObject()
                .createObject()).thenApply(resp -> {
            if (resp.getResponseCode() != 201) throw new ResponseEvaluationException(resp);
            return null;
        });
    }

    /**
     * Renames an existing MMO character of the current user.
     *
     * @param characterId The character's id.
     * @param name The new name.
     * @return A completable future that will complete with true, if the renaming was successful, false if the renaming is still on cooldown.
     */
    @AuthRequired
    @Scopes("ls5-write")
    public CompletableFuture<Void> renameCharacter(long characterId, String name) {
        return api.post("api/ls5/rename-character", JsonBuilder.object()
                .set("characterId", characterId)
                .set("newName", name)
                .createObject()).thenAccept(resp -> {
            if (resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
        });
    }

    /**
     * Delete an existing MMO character of the current user.
     *
     * @param characterId The character's id.
     * @return A completable future that will complete, when the character was deleted.
     */
    @AuthRequired
    @Scopes("ls5-write")
    public CompletableFuture<Void> deleteCharacter(long characterId) {
        return api.sendAPIRequest("api/ls5/delete-character", "DELETE", JsonBuilder.object()
                .set("characterId", characterId)
                .createObject()).thenApply(resp -> {
            if (resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            return null;
        });
    }

    /**
     * Set the active character of the current user.
     *
     * @param characterId The character's id.
     * @return A completable future that will complete, when the character was changed.
     */
    @AuthRequired
    @Scopes("ls5-write")
    public CompletableFuture<Void> setActiveCharacter(long characterId) {
        return api.post("api/ls5/set-active-character", JsonBuilder.object()
                .set("characterId", characterId)
                .createObject()).thenApply(resp -> {
            if (resp.getResponseCode() != 200) throw new ResponseEvaluationException(resp);
            return null;
        });
    }
}
