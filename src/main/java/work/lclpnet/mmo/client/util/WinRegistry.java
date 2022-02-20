/* Source: https://github.com/apache/npanday/tree/trunk/components/dotnet-registry/src/main/java/npanday/registry */
package work.lclpnet.mmo.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.prefs.Preferences;

@Environment(EnvType.CLIENT)
public class WinRegistry {
    public static final int HKEY_CURRENT_USER = 0x80000001;
    public static final int HKEY_LOCAL_MACHINE = 0x80000002;
    public static final int REG_SUCCESS = 0;

    private static final int KEY_READ = 0x20019;
    private static final Preferences userRoot = Preferences.userRoot();
    private static final Preferences systemRoot = Preferences.systemRoot();
    private static final Class<? extends Preferences> userClass = userRoot.getClass();
    private static final Method regOpenKey;
    private static final Method regCloseKey;
    private static final Method regQueryValueEx;

    static {
        try {
            regOpenKey = userClass.getDeclaredMethod("WindowsRegOpenKey",
                    int.class, byte[].class, int.class);
            regOpenKey.setAccessible(true);
            regCloseKey = userClass.getDeclaredMethod("WindowsRegCloseKey", int.class);
            regCloseKey.setAccessible(true);
            regQueryValueEx = userClass.getDeclaredMethod("WindowsRegQueryValueEx",
                    int.class, byte[].class);
            regQueryValueEx.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WinRegistry() {
    }

    /**
     * Read a value from key and value name
     *
     * @param hkey      HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key       The registry key.
     * @param valueName The registry value.
     * @return The read registry value.
     * @throws IllegalArgumentException If there was an error.
     * @throws IllegalAccessException If there was an error.
     * @throws InvocationTargetException If there was an error.
     */
    public static String readString(int hkey, String key, String valueName)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == HKEY_LOCAL_MACHINE) {
            return readString(systemRoot, hkey, key, valueName);
        } else if (hkey == HKEY_CURRENT_USER) {
            return readString(userRoot, hkey, key, valueName);
        } else {
            throw new IllegalArgumentException("hkey=" + hkey);
        }
    }

    // =====================

    private static String readString(Preferences root, int hKey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root,
                new Object[]{hKey, toCstr(key), KEY_READ});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[]{handles[0], toCstr(value)});
        regCloseKey.invoke(root, handles[0]);
        return (valb != null ? new String(valb).trim() : null);
    }

    // utility
    private static byte[] toCstr(String str) {
        byte[] result = new byte[str.length() + 1];

        for (int i = 0; i < str.length(); i++) {
            result[i] = (byte) str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
}
