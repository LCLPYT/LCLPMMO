package work.lclpnet.mmo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.character.MMOCharacter;

public class User extends JsonSerializeable{

	private int id;
	private String name;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static MMOCharacter selectedCharacter = null;
	@OnlyIn(Dist.CLIENT)
	public static User current = null;
	
	@OnlyIn(Dist.CLIENT)
	public static void reloadUser(User user, Runnable callback) {
		if(user == null || FMLEnvironment.dist != Dist.CLIENT) return;
		
		User.current = user;
		
		JsonObject body = new JsonObject();
		body.addProperty("userId", user.getId());
		
		LCLPNetwork.post("api/ls5/get-active-character", body, resp -> {
			if(resp.isNoConnection()) {
				User.selectedCharacter = null;
				callback.run();
			}
			else if(resp.getResponseCode() != 200) {
				System.err.println(resp);
				User.selectedCharacter = null;
				callback.run();
			} else {
				JsonObject obj = JsonSerializeable.parse(resp.getRawResponse(), JsonObject.class);
				JsonElement elem = obj.get("extra");
				if(elem == null || elem.isJsonNull()) User.selectedCharacter = null;
				else User.selectedCharacter = JsonSerializeable.cast(elem.getAsJsonObject(), MMOCharacter.class);
				callback.run();
			}
		});
	}
	
}
