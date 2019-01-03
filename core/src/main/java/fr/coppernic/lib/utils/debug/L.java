package fr.coppernic.lib.utils.debug;

import fr.bipi.tressence.common.utils.Info;
import timber.log.Timber;

import static fr.coppernic.lib.utils.io.BytesHelper.byteArrayToString;

/**
 * utility class for log debugging
 * <p>Created on 22/05/17
 *
 * @author Bastien Paul
 */
@SuppressWarnings("WeakerAccess")
public class L {
    private static int sDepth = 2;

    /**
     * Log the method name from which m() is called.
     *
     * @param tag TAG to use in android log
     */
    public static void m(String tag) {
        Timber.tag(tag);
        Timber.v(Info.getMethodName(sDepth));
    }

    /**
     * Log the method name from which m() is called.
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     */
    public static void m(String tag, boolean debug) {
        if (debug) {
            Timber.tag(tag);
            Timber.v(Info.getMethodName(sDepth));
        }
    }

    /**
     * Log the method name from which m() is called.
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     * @param msg   Additional message
     */
    public static void m(String tag, boolean debug, String msg) {
        if (debug) {
            Timber.tag(tag);
            Timber.v(Info.getMethodName(sDepth) + ", " + msg);
        }
    }

    /**
     * Log the method name from which m() is called with the current thread information
     *
     * @param tag TAG to use in android log
     */
    public static void mt(String tag) {
        Timber.tag(tag);
        Timber.v(Info.getMethodName(sDepth) + ", " + Info.getThreadInfoString());
    }

    /**
     * Log the method name from which m() is called with the current thread information
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     */
    public static void mt(String tag, boolean debug) {
        if (debug) {
            Timber.tag(tag);
            Timber.v(Info.getMethodName(sDepth) + ", " + Info.getThreadInfoString());
        }
    }

    /**
     * Log the method name from which m() is called with the current thread information
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     */
    public static void mt(String tag, boolean debug, String msg) {
        if (debug) {
            Timber.tag(tag);
            Timber.v(Info.getMethodName(sDepth) + ", " + Info.getThreadInfoString() + ", " + msg);
        }
    }

    /**
     * Print a byte array in Android logcat
     *
     * @param tag   TAG
     * @param array Array to print
     * @param len   len of array
     */
    public static void printLine(String tag, byte[] array, int len) {
        Timber.tag(tag);
        Timber.v("l: %d, d: %s", len, byteArrayToString(array, len));
    }

}
