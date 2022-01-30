package work.lclpnet.mmo.network;

import net.fabricmc.loader.api.FabricLoader;
import work.lclpnet.mmo.Config;
import work.lclpnet.mmo.network.backend.LCLPNetworkSession;
import work.lclpnet.storage.LocalLCLPStorage;

import java.io.*;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class AccessTokenLoader {

    public static CompletableFuture<String> load() {
        return CompletableFuture.supplyAsync(() -> {
            File f;
            try {
                f = getTokenFile();
            } catch (IOException e) {
                throw new CompletionException(e);
            }

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
        });
    }

    public static File getTokenFile() throws IOException {
        return new File(getDirectory(), "lclpnetwork.token");
    }

    private static File getDirectory() throws IOException {
        String env = Config.getNetworkingProvider();
        String dist = FabricLoader.getInstance().getEnvironmentType().name().toLowerCase(Locale.ROOT);
        return LocalLCLPStorage.getDirectory("lclpmmo", "access_tokens", env, dist);
    }
}
