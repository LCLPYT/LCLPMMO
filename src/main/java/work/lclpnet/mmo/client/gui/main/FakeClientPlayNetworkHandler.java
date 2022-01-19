package work.lclpnet.mmo.client.gui.main;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.Session;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class FakeClientPlayNetworkHandler extends ClientPlayNetworkHandler {

    private PlayerListEntry playerInfo;

    public FakeClientPlayNetworkHandler(MinecraftClient client) {
        super(client, client.currentScreen, new FakeClientConnection(NetworkSide.CLIENTBOUND), client.getSession().getProfile());

        Session session = client.getSession();
        PlayerListS2CPacket.Entry entry = new PlayerListS2CPacket().
                new Entry(getProfile(), 0, GameMode.SURVIVAL, new LiteralText(session.getUsername()));

        this.playerInfo = new PlayerListEntry(entry);
    }

    @Nullable
    @Override
    public PlayerListEntry getPlayerListEntry(UUID uuid) {
        return this.playerInfo;
    }

    @Nullable
    @Override
    public PlayerListEntry getPlayerListEntry(String profileName) {
        return this.playerInfo;
    }
}
