package work.lclpnet.mmo.asm.helpers;

import java.util.function.Consumer;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.asm.type.IMMOUser;
import work.lclpnet.mmo.util.AuthHelper;

public class HelperServerLoginNetHandler {

	public static boolean ifOnClient(MinecraftServer server, GameProfile profile, ServerPlayerEntity serverplayerentity,
			ServerLoginNetHandler handler, GameProfile loginGameProfile, NetworkManager networkManager, 
			Consumer<ServerPlayerEntity> playerSetter) {
		if(FMLEnvironment.dist != Dist.CLIENT) return false;
		
		if(server instanceof IntegratedServer && ((IntegratedServer) server).isServerOwner(profile)) {
			if (serverplayerentity != null) {
				try {
					AuthHelper.setLoginStateDelayAccept(handler);
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
				ServerPlayerEntity player = server.getPlayerList().createPlayerForUser(loginGameProfile);
				playerSetter.accept(player);
				IMMOUser.initMyPlayer(player);
			} else {
				ServerPlayerEntity created = server.getPlayerList().createPlayerForUser(loginGameProfile);
				IMMOUser.initMyPlayer(created);
				
				server.getPlayerList().initializeConnectionToPlayer(networkManager, created);
			}
			return true;
		}
		
		return false;
	}
	
}
