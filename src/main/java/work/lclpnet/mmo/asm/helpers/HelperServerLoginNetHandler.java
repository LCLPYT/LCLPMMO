package work.lclpnet.mmo.asm.helpers;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.network.login.ServerLoginNetHandler.State;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.facade.User;
import work.lclpnet.mmo.facade.character.MMOCharacter;

import java.util.function.Consumer;

public class HelperServerLoginNetHandler {

	public static boolean ifOnClient(MinecraftServer server, GameProfile profile, ServerPlayerEntity serverplayerentity,
			ServerLoginNetHandler handler, GameProfile loginGameProfile, NetworkManager networkManager, 
			Consumer<ServerPlayerEntity> playerSetter) {
		if(FMLEnvironment.dist != Dist.CLIENT) return false;
		
		if(server instanceof IntegratedServer && server.isServerOwner(profile)) {
			resolve(serverplayerentity, handler, server, loginGameProfile, networkManager, User.getSelectedCharacter(), User.getCurrent(), playerSetter);
			return true;
		}
		
		return false;
	}
	
	public static void resolve(ServerPlayerEntity serverplayerentity, ServerLoginNetHandler handler, MinecraftServer server,
			GameProfile loginGameProfile, NetworkManager networkManager, MMOCharacter character, User tmpUser, 
			Consumer<ServerPlayerEntity> playerSetter) {
		
		networkManager.sendPacket(new SLoginSuccessPacket(loginGameProfile));
		
		if (serverplayerentity != null) {
			handler.currentLoginState = State.DELAY_ACCEPT;
			
			ServerPlayerEntity player = server.getPlayerList().createPlayerForUser(loginGameProfile);
			playerSetter.accept(player);
			IMMOUser mmo = IMMOUser.getMMOUser(player);
			mmo.setMMOCharacter(character);
			mmo.setUser(tmpUser);
		} else {
			ServerPlayerEntity created = server.getPlayerList().createPlayerForUser(loginGameProfile);
			IMMOUser mmo = IMMOUser.getMMOUser(created);
			mmo.setMMOCharacter(character);
			mmo.setUser(tmpUser);
			
			server.getPlayerList().initializeConnectionToPlayer(networkManager, created);
		}
	}
	
}
