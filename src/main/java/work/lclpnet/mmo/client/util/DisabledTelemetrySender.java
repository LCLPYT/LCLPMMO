package work.lclpnet.mmo.client.util;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.telemetry.TelemetrySender;

import java.util.Optional;
import java.util.UUID;

/**
 * Disable telemetry on this instance through MixinTelemetrySender, where this class is instance-checked.
 */
public class DisabledTelemetrySender extends TelemetrySender {

    public DisabledTelemetrySender(MinecraftClient client, UserApiService userApiService, Optional<String> userId, Optional<String> clientId, UUID deviceSessionId) {
        super(client, userApiService, userId, clientId, deviceSessionId);
    }
}
