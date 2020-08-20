package fr.coppernic.lib.utils.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.coppernic.lib.utils.BuildConfig;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class LogDefines {

    // Log
    public static final String TAG =
        BuildConfig.LIBRARY_PACKAGE_NAME.substring(
            BuildConfig.LIBRARY_PACKAGE_NAME.lastIndexOf("."));
    public static final Logger LOG = LoggerFactory.getLogger(TAG);
    /**
     * True to activate verbose logging in all lib
     */
    public static Boolean verbose = false;
    /**
     * True to activate profiler in all lib
     */
    public static Boolean profile = false;

    private LogDefines() {
    }

    public static void setVerbose(Boolean b) {
        verbose = b;
    }

    public static void setProfile(Boolean b) {
        profile = b;
    }
}
