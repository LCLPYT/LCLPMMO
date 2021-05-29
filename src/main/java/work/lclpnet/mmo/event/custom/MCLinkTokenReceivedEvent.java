package work.lclpnet.mmo.event.custom;

import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class MCLinkTokenReceivedEvent extends Event {

    protected final ServerLoginNetHandler handler;
    protected final String token;

    public MCLinkTokenReceivedEvent(ServerLoginNetHandler handler, String token) {
        if (FMLEnvironment.dist != Dist.DEDICATED_SERVER)
            throw new IllegalStateException(String.format("Can't instantiate %s in invalid dist %s.", MCLinkTokenReceivedEvent.class.getSimpleName(), FMLEnvironment.dist));
        this.handler = handler;
        this.token = token;
    }

    public ServerLoginNetHandler getHandler() {
        return handler;
    }

    public String getToken() {
        return token;
    }
}
