package work.lclpnet.mmo.network.msg;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import work.lclpnet.mmo.audio.MusicSystem;
import work.lclpnet.mmo.network.IMessage;
import work.lclpnet.mmo.network.IMessageSerializer;

import java.util.function.Supplier;

public class MessageMMOMusic implements IMessage {

    private boolean stop = false;
    private boolean loop = false;
    private final SoundEvent music;

    public MessageMMOMusic(SoundEvent music) {
        this(music, false, false);
    }

    public MessageMMOMusic(SoundEvent music, boolean stop, boolean loop) {
        this.music = music;
        this.stop = stop;
        this.loop = loop;
    }

    @OnlyIn(Dist.CLIENT)
    private void setLoop() {
        MusicSystem.setLoopBackgroundMusic(loop);
    }

    @OnlyIn(Dist.CLIENT)
    private void stopMusic() {
        MusicSystem.setLoopBackgroundMusic(false);
        MusicSystem.playBackgroundMusic(null);
    }

    @OnlyIn(Dist.CLIENT)
    private void playMusic() {
        if (music != null) MusicSystem.playBackgroundMusic(music);
    }

    @Override
    public void handle(Supplier<Context> ctx) {
        if (FMLEnvironment.dist != Dist.CLIENT) return;

        setLoop();
        if (this.stop) stopMusic();
        else playMusic();
    }

    public static class Serializer implements IMessageSerializer<MessageMMOMusic> {

        @Override
        public void encode(MessageMMOMusic message, PacketBuffer buffer) {
            buffer.writeBoolean(message.stop);
            buffer.writeBoolean(message.loop);

            boolean nonNull = message.music != null;
            buffer.writeBoolean(nonNull);
            if (nonNull) buffer.writeResourceLocation(message.music.name);
        }

        @Override
        public MessageMMOMusic decode(PacketBuffer buffer) {
            boolean stop = buffer.readBoolean();
            boolean loop = buffer.readBoolean();
            boolean nonNull = buffer.readBoolean();
            SoundEvent music = null;
            if (nonNull) music = new SoundEvent(buffer.readResourceLocation());
            return new MessageMMOMusic(music, stop, loop);
        }
    }
}
