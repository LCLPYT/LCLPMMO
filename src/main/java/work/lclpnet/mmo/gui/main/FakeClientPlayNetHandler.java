package work.lclpnet.mmo.gui.main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.network.PacketDirection;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket.AddPlayerData;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class FakeClientPlayNetHandler extends ClientPlayNetHandler {

    private NetworkPlayerInfo playerInfo = null;

    public FakeClientPlayNetHandler(Minecraft mcIn) {
        super(mcIn,
                mcIn.currentScreen,
                new FakeNetworkManager(PacketDirection.CLIENTBOUND),
                mcIn.getSession().getProfile());

        AddPlayerData entry = new SPlayerListItemPacket().
                new AddPlayerData(getGameProfile(), 0, GameType.SURVIVAL, new StringTextComponent(mcIn.getSession().getUsername()));
        this.playerInfo = new NetworkPlayerInfo(entry);
    }

    @Override
    public NetworkPlayerInfo getPlayerInfo(UUID uniqueId) {
        return this.playerInfo;
    }

    @Override
    public NetworkPlayerInfo getPlayerInfo(String name) {
        return this.playerInfo;
    }
}
