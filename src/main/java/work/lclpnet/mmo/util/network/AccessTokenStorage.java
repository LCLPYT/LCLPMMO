package work.lclpnet.mmo.util.network;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.util.LocalLCLPStorage;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AccessTokenStorage {

    public static CompletableFuture<Void> load() {
        return CompletableFuture.supplyAsync(() -> {
            File f = getTokenFileForEnv();
            if (!f.exists()) throw new CompletionException(new IOException("Token file does not exist."));

            try (InputStream in = new FileInputStream(f);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer, 0, buffer.length)) != -1)
                    out.write(buffer, 0, read);

                return out.toString();
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }).thenCompose(LCLPNetwork::setAccessToken);
    }

    @OnlyIn(Dist.CLIENT)
    public static CompletableFuture<Void> store(@Nullable String token) {
        return LCLPNetwork.setAccessToken(token).thenCompose(voidResult -> CompletableFuture.runAsync(() -> {
            File f = getTokenFileForEnv();
            if (token == null) {
                if (f.exists()) {
                    // delete token; if successful, token was successfully unset
                    if (!f.delete()) throw new CompletionException(new IOException("Could not delete token file"));
                }
            } else {
                if (!f.exists()) { // if file does not exist, create it
                    if (!f.getParentFile().exists() && !f.getParentFile().mkdirs()) throw new IllegalStateException("Could not create directory.");

                    File readme = new File(f.getParentFile(), "README.txt");
                    try (OutputStream out = new FileOutputStream(readme)) {
                        out.write(I18n.format("warn.auth.token").getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                try (OutputStream out = new FileOutputStream(f)) {
                    out.write(token.getBytes());
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }
        }));
    }

    private static File getTokenFileForEnv() {
        return new File(getDirectoryForEnv(), "lclpnetwork.token");
    }

    private static File getDirectoryForEnv() {
        String env = Config.isNetworkStagingMode() ? "staging" : "live";
        String dist = FMLEnvironment.dist.name().toLowerCase(Locale.ROOT);
        return LocalLCLPStorage.getDirectory("lclpmmo", "access_tokens", env, dist);
    }
}
