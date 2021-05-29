package work.lclpnet.mmo.facade;

import com.google.gson.JsonObject;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.network.HTTPResponse;
import work.lclpnet.mmo.util.network.LCLPNetwork;

public class User extends JsonSerializeable {

    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    private static MMOCharacter selectedCharacter;
    private static User current;

    public static User getCurrent() {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        return current;
    }

    public static MMOCharacter getSelectedCharacter() {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        return selectedCharacter;
    }

    public static void setCurrent(User current) {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        User.current = current;
    }

    public static void setSelectedCharacter(MMOCharacter selectedCharacter) {
        if (FMLEnvironment.dist != Dist.CLIENT) throw new RuntimeException("Wrong side.");
        User.selectedCharacter = selectedCharacter;
    }

    @OnlyIn(Dist.CLIENT)
    public static void reloadUser(Runnable callback) {
        reloadUser(User.current, callback);
    }

    @OnlyIn(Dist.CLIENT)
    public static void reloadUser(User user, Runnable callback) {
        if (user == null) {
            callback.run();
            return;
        }

        User.current = user;

        JsonObject body = new JsonObject();
        body.addProperty("userId", user.getId());
        body.addProperty("fetchData", true);

        LCLPNetwork.post("api/ls5/get-active-character", body, resp -> {
            User.selectedCharacter = handleActiveCharacterResponse(resp);
            callback.run();
        });
    }

    public static MMOCharacter handleActiveCharacterResponse(HTTPResponse resp) {
        if (resp.isNoConnection()) {
            return null;
        } else if (resp.getResponseCode() == 406 && resp.hasJsonStatusMessage()
                && "LCLPServer5.0 is not initialized for this user.".equals(resp.getJsonStatusMessage())) {
            return null;
        } else if (resp.getResponseCode() != 200) {
            System.err.println(resp);
            return null;
        } else {
            return resp.getExtra(MMOCharacter.class);
        }
    }

    public static User handleUserResponse(HTTPResponse resp) {
        if (resp.isNoConnection()) {
            return null;
        } else if (resp.getResponseCode() != 200) {
            System.err.println(resp);
            return null;
        } else {
            return resp.getExtra(User.class);
        }
    }
}
