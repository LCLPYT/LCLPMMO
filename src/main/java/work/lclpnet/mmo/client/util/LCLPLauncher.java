package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Environment(EnvType.CLIENT)
public class LCLPLauncher {

    private static final Logger LOGGER = LogManager.getLogger();

    public static CompletableFuture<String> getLCLPLauncherExecutable() {
        return CompletableFuture.supplyAsync(() -> {
            final String property = System.getProperty("lclplauncher.program");
            if (property != null) return property;

            if (System.getProperty("os.name").startsWith("Windows")) {
                final String WIN_EXE_NAME = "LCLPLauncher.exe",
                        WIN_EXE_PATH = System.getProperty("user.home") + "\\AppData\\Local\\Programs\\LCLPLauncher\\" + WIN_EXE_NAME;

                // check for default location on windows
                File winDefault = new File(WIN_EXE_PATH);
                if (winDefault.isFile()) return winDefault.getAbsolutePath();

                // try to read executable path from registry
                try {
                    String path = tryReadPathFromRegistry();
                    if (path != null) {
                        File exe = new File(path, WIN_EXE_NAME);
                        if (exe.isFile()) return exe.getAbsolutePath();
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    // log the error and continue to try...
                    LOGGER.debug("Error while reading from windows registry", e);
                }

                // try to read path from terminal
                String path = tryReadPathFromTerminal("where");
                if (path != null) {
                    File exe = new File(path);
                    if (exe.isFile()) return exe.getAbsolutePath();
                }
            }

            // try to read path from terminal
            String path = tryReadPathFromTerminal("which");
            if (path == null) return null;

            File exe = new File(path);
            return exe.isFile() ? exe.getAbsolutePath() : null;
        });
    }

    public static CompletableFuture<Void> startLCLPLauncher() {
        return getLCLPLauncherExecutable().thenApply(exe -> {
            try {
                Runtime.getRuntime().exec(String.format("%s --location /library/app/ls5", exe));
            } catch (IOException e) {
                throw new CompletionException(e);
            }
            return null;
        });
    }

    @Nullable
    private static String tryReadPathFromTerminal(String command) {
        try {
            Process p = new ProcessBuilder(command, "lclplauncher")
                    .redirectErrorStream(true)
                    .start();

            String firstLine;
            try (BufferedReader processOutputReader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                firstLine = processOutputReader.readLine();
                p.waitFor();
            }

            if (firstLine != null) return firstLine.trim();
        } catch (IOException | InterruptedException e) {
            throw new CompletionException(e);
        }

        return null;
    }

    @Nullable
    private static String tryReadPathFromRegistry() throws InvocationTargetException, IllegalAccessException {
        return WinRegistry.readString(
                WinRegistry.HKEY_CURRENT_USER,
                "Software\\lclplauncher",
                "InstallLocation"
        );
    }
}
