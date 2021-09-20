package work.lclpnet.mmo.network.msg;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;
import work.lclpnet.mmo.network.MMOPacketHandler;
import work.lclpnet.mmo.util.network.LCLPNetwork;

import java.util.function.Supplier;

public class MessageRequestMCLink implements IMessage {

    @Override
    public void handle(Supplier<Context> ctx) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;

        LCLPNetwork.getAPI().requestMCLinkToken()
                .thenAccept(token -> MMOPacketHandler.INSTANCE.reply(new MessageSendMCLinkToken(token), ctx.get()))
                .exceptionally(err -> {
                    err.printStackTrace();
                    return null;
                });
    }

    public static class Serializer implements IMessageSerializer<MessageRequestMCLink> {

        @Override
        public void encode(MessageRequestMCLink message, PacketBuffer buffer) {
        }

        @Override
        public MessageRequestMCLink decode(PacketBuffer buffer) {
            return new MessageRequestMCLink();
        }
    }
}
