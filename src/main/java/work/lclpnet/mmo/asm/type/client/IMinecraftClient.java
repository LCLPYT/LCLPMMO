package work.lclpnet.mmo.asm.type.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import work.lclpnet.mmo.client.util.DisabledTelemetrySender;

@Environment(EnvType.CLIENT)
public interface IMinecraftClient {

    DisabledTelemetrySender lclpmmo$createDisabledTelemetrySender();
}
