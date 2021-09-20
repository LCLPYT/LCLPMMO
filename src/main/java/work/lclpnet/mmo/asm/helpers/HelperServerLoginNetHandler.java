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
import work.lclpnet.mmo.facade.DummyMMOUser;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import java.util.function.Consumer;

public class HelperServerLoginNetHandler {

    public static boolean ifOnClient(MinecraftServer server, GameProfile profile, ServerPlayerEntity serverplayerentity,
                                     ServerLoginNetHandler handler, GameProfile loginGameProfile, NetworkManager networkManager,
                                     Consumer<ServerPlayerEntity> playerSetter) {
        if (FMLEnvironment.dist != Dist.CLIENT) return false;

        if (server instanceof IntegratedServer && server.isServerOwner(profile)) {
            IMMOUser mmo = new DummyMMOUser();
            mmo.setUser(LCLPNetwork.getUser());
            mmo.setMMOCharacter(LCLPNetwork.getSelectedCharacter());
            resolve(serverplayerentity, handler, server, loginGameProfile, networkManager, mmo, playerSetter);
            return true;
        }

        return false;
    }

    public static void resolve(ServerPlayerEntity serverplayerentity, ServerLoginNetHandler handler, MinecraftServer server,
                               GameProfile loginGameProfile, NetworkManager networkManager, IMMOUser user,
                               Consumer<ServerPlayerEntity> playerSetter) {

        networkManager.sendPacket(new SLoginSuccessPacket(loginGameProfile));

        if (serverplayerentity != null) {
            handler.currentLoginState = State.DELAY_ACCEPT;

            ServerPlayerEntity player = server.getPlayerList().createPlayerForUser(loginGameProfile);
            playerSetter.accept(player);
            IMMOUser mmo = IMMOUser.getMMOUser(player);
            mmo.setMMOCharacter(user.getMMOCharacter());
            mmo.setUser(user.getUser());
        } else {
            ServerPlayerEntity created = server.getPlayerList().createPlayerForUser(loginGameProfile);
            IMMOUser mmo = IMMOUser.getMMOUser(created);
            mmo.setMMOCharacter(user.getMMOCharacter());
            mmo.setUser(user.getUser());

            server.getPlayerList().initializeConnectionToPlayer(networkManager, created);
        }
    }
}
