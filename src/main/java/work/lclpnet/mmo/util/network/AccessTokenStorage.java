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
import java.util.function.Consumer;

public class AccessTokenStorage {

    public static void load(final Consumer<Boolean> callback) {
        new Thread(() -> {
            File f = getTokenFileForEnv();
            if (!f.exists()) {
                callback.accept(false);
                return;
            }

            try (InputStream in = new FileInputStream(f);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer, 0, buffer.length)) != -1)
                    out.write(buffer, 0, read);

                LCLPNetwork.setAccessToken(out.toString());
                callback.accept(true);
            } catch (IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        }, "access token loader").start();
    }

    @OnlyIn(Dist.CLIENT)
    public static void store(@Nullable String token, final Consumer<Boolean> callback) {
        LCLPNetwork.setAccessToken(token);

        new Thread(() -> {
            File f = getTokenFileForEnv();
            if (!f.exists()) {
                if (token == null) {
                    callback.accept(true);
                    return;
                }

                if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
                    throw new IllegalStateException("Could not create directory.");

                File readme = new File(f.getParentFile(), "README.txt");
                try (OutputStream out = new FileOutputStream(readme)) {
                    out.write(I18n.format("warn.auth.token").getBytes(StandardCharsets.UTF_8));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (f.exists() && token == null) {
                callback.accept(f.delete());
                return;
            }

            try (OutputStream out = new FileOutputStream(f)) {
                out.write(token.getBytes());
                callback.accept(true);
            } catch (IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        }, "access token saver").start();
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
