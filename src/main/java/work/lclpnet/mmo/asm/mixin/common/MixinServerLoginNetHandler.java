package work.lclpnet.mmo.asm.mixin.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import work.lclpnet.mmo.asm.helpers.HelperServerLoginNetHandler;
import work.lclpnet.mmo.facade.JsonSerializeable;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;
import work.lclpnet.mmo.util.network.HTTPResponse;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import java.util.function.Consumer;

@Mixin(ServerLoginNetHandler.class)
public class MixinServerLoginNetHandler {

	@Shadow
	private ServerPlayerEntity player;
	@Shadow
	@Final
	private MinecraftServer server;
	@Shadow
	public GameProfile loginGameProfile;
	@Shadow
	@Final
	public NetworkManager networkManager;
	
	private User tmpUser = null;

	@Inject(
			method = "tryAcceptPlayer()V",
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
		final GameProfile profile = handler.loginGameProfile != null ? (handler.loginGameProfile.getId() != null ? handler.loginGameProfile : null) : null;
		
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
			else if(charResp.getResponseCode() == 406) {
				handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.no_character"));
			}
			else if(charResp.getResponseCode() != 200) {
				handler.disconnect(new StringTextComponent("Internal server error."));
				System.err.println(charResp);
			}
			else {
				MMOCharacter character = charResp.getExtra(MMOCharacter.class);
				if(character == null) {
					handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.no_character"));
					return;
				}
				
				HelperServerLoginNetHandler.resolve(serverplayerentity, handler, this.server, loginGameProfile, this.networkManager, 
						character, this.tmpUser, x -> this.player = x);
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
				charBody.addProperty("fetchData", true);
				
				LCLPNetwork.post("api/ls5/get-active-character", charBody, charConsumer);
			}
		};

		LCLPNetwork.post("api/mc/get-user", usrBody, usrResp -> {
			if(usrResp.isNoConnection()) handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.lclpnetwork_down"));
			else if(usrResp.getResponseCode() == 404) {
				handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_linked"));
			}
			else if(usrResp.getResponseCode() != 200) {
				handler.disconnect(new StringTextComponent("Internal server error."));
				System.err.println(usrResp);
			}
			else {
				JsonObject obj = JsonSerializeable.parse(usrResp.getRawResponse(), JsonObject.class);
				JsonElement idElem = obj.get("user_id");
				if(idElem == null) {
					handler.disconnect(new TranslationTextComponent("multiplayer.disconnect.not_linked"));
					return;
				}

				JsonObject idBody = new JsonObject();
				idBody.addProperty("userId", idElem.getAsInt());
				
				LCLPNetwork.post("api/auth/user-by-id", idBody, idConsumer);
			}
		});
	}
	
	@Redirect(
			method = "tryAcceptPlayer()V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/NetworkManager;sendPacket(Lnet/minecraft/network/IPacket;)V"
					)
			)
	public void onAcceptPlayerRemLoginSuccess(NetworkManager nm, IPacket<?> packet) {
		// this empty redirect is necessary, because otherwise the user would not see login errors. We will set this later in HelperServerLoginNetHandler.resolve
	}

}
