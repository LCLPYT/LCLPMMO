package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import work.lclpnet.mmo.network.AccessTokenLoader;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Environment(EnvType.CLIENT)
public class ClientAccessTokenSaver {

    public static CompletableFuture<Void> store(@Nullable String token) {
        return LCLPNetworkSession.init(token).thenRunAsync(() -> {
            File file;
            try {
                file = AccessTokenLoader.getTokenFile();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            if (token == null) {
                if (file.exists()) {
                    // delete token; if successful, token was successfully unset
                    if (!file.delete()) throw new CompletionException(new IOException("Could not delete token file"));
                }
            } else {
                if (!file.exists()) { // if file does not exist, create it
                    if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                        throw new IllegalStateException("Could not create directory.");

                    File readme = new File(file.getParentFile(), "README.txt");
                    try (OutputStream out = new FileOutputStream(readme)) {
                        out.write(I18n.translate("warn.auth.token").getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try (OutputStream out = new FileOutputStream(file)) {
                    out.write(token.getBytes());
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }
        });
    }
}
