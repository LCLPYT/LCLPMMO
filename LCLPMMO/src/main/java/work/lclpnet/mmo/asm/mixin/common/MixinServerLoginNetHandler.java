package work.lclpnet.mmo.asm.mixin.common;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.asm.helpers.HelperServerLoginNetHandler;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.AuthHelper;
import work.lclpnet.mmo.util.HTTPResponse;
import work.lclpnet.mmo.util.LCLPNetwork;
import work.lclpnet.mmo.util.User;

@Mixin(ServerLoginNetHandler.class)
public class MixinServerLoginNetHandler {

	@Shadow
	private ServerPlayerEntity player;
	@Shadow
	@Final
	private MinecraftServer server;
	@Shadow
	private GameProfile loginGameProfile;
	@Shadow
	@Final
	public NetworkManager networkManager;
	
	private User tmpUser = null;

	@Inject(
			method = "Lnet/minecraft/network/login/ServerLoginNetHandler;tryAcceptPlayer()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/management/PlayerList;getPlayerByUUID(Ljava/util/UUID;)Lnet/minecraft/entity/player/ServerPlayerEntity;",
					shift = Shift.BY,
					by = 2
					),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
			)
	public void onAcceptPlayer(CallbackInfo ci, ITextComponent itextcomponent, final ServerPlayerEntity serverplayerentity) {
		final ServerLoginNetHandler handler = (ServerLoginNetHandler) (Object) this;
		final GameProfile profile = AuthHelper.getGameProfile(handler);
		
		ci.cancel();

		// If in singleplayer (integrated server)
		if(FMLEnvironment.dist == Dist.CLIENT && 
				HelperServerLoginNetHandler.ifOnClient(this.server, profile, serverplayerentity, handler, 
						this.loginGameProfile, this.networkManager, x -> this.player = x)) 
			return;
		
		JsonObject usrBody = new JsonObject();
		usrBody.addProperty("uuid", profile.getId().toString());

		Consumer<HTTPResponse> charConsumer = charResp -> {
			if(charResp.isNoConnection()) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
			else if(charResp.getResponseCode() != 200) {
				handler.disconnect(new StringTextComponent("Internal server error."));
				System.err.println(charResp);
			}
			else {
				MMOCharacter character = charResp.getExtra(MMOCharacter.class);
				if(character == null) {
					handler.disconnect(new StringTextComponent("Internal server error."));
					System.err.println("Character is null.");
					return;
				}
				
				if (serverplayerentity != null) {
					try {
						AuthHelper.setLoginStateDelayAccept(handler);
					} catch (ReflectiveOperationException e) {
						e.printStackTrace();
					}
					this.player = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
					IMMOUser mmo = IMMOUser.getMMOUser(this.player);
					mmo.setMMOCharacter(character);
					mmo.setUser(tmpUser);
				} else {
					ServerPlayerEntity created = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
					IMMOUser mmo = IMMOUser.getMMOUser(created);
					mmo.setMMOCharacter(character);
					mmo.setUser(tmpUser);
					
					this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, created);
				}
			}
		};

		Consumer<HTTPResponse> idConsumer = idResp -> {
			if(idResp.isNoConnection()) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
			else if(idResp.getResponseCode() != 200) {
				handler.disconnect(new StringTextComponent("Internal server error."));
				System.err.println(idResp);
			}
			else {
				User user = idResp.getExtra(User.class);
				if(user == null) {
					handler.disconnect(new StringTextComponent("Internal server error."));
					System.err.println("User is null.");
					return;
				}
				
				tmpUser = user;
				
				JsonObject charBody = new JsonObject();
				charBody.addProperty("userId", user.getId());

				LCLPNetwork.post("api/ls5/get-active-character", charBody, charConsumer);
			}
		};

		LCLPNetwork.post("api/mc/get-user", usrBody, usrResp -> {
			if(usrResp.isNoConnection()) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
			else if(usrResp.getResponseCode() != 200) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_logged_in"));
			else {
				JsonObject obj = JsonSerializeable.parse(usrResp.getRawResponse(), JsonObject.class);
				JsonElement idElem = obj.get("user_id");
				if(idElem == null) {
					handler.disconnect(new StringTextComponent("Internal server error."));
					System.err.println("'user_id' element not found.");
					return;
				}

				JsonObject idBody = new JsonObject();
				idBody.addProperty("userId", idElem.getAsInt());

				LCLPNetwork.post("api/auth/user-by-id", idBody, idConsumer);
			}
		});
	}

}
