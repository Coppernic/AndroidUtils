package fr.coppernic.lib.utils.os;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;

/**
 * Mirrors hidden class {android.os.SystemProperties} (available since API Level 1).
 * <p>
 * Created on 22/12/16
 *
 * @author bastien
 */
public class SystemProperties {

    private SystemProperties() {
    }

    @SuppressWarnings("unchecked")
    @SuppressLint("PrivateApi")
    public static void set(String key, String value) throws OsException {
        try {
            final Class SystemProperties = Class.forName("android.os.SystemProperties");
            final Method set1 = SystemProperties.getMethod("set", String.class, String.class);
            set1.invoke(SystemProperties, key, value);
        } catch (Exception e) {
            throw new OsException(e);
        }
    }

    /**
     * Gets system properties set by <code>adb shell setprop <em>key</em> <em>value</em></code>
     *
     * @param key          the property key.
     * @param defaultValue the value to return if the property is undefined or empty
     *                     (this parameter may be {@code null}).
     * @return the system property value or the default value.
     */
    @SuppressLint("PrivateApi")
    public static String get(String key, String defaultValue) {
        try {
            final Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            final Method get = systemProperties.getMethod("get", String.class, String.class);
            return (String) get.invoke(null, key, defaultValue);
        } catch (Exception e) {
            // This should never happen
            return defaultValue;
        }
    }
}
