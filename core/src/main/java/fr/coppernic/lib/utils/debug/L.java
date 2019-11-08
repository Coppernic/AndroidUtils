package fr.coppernic.lib.utils.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.bipi.tressence.common.utils.Info;

import static fr.coppernic.lib.utils.io.BytesHelper.byteArrayToString;

/**
 * utility class for log debugging
 * <p>Created on 22/05/17
 *
 * @author Bastien Paul
 */
@SuppressWarnings("WeakerAccess")
public final class L {
    public static int sDepth = 3;

    private L() {
    }

    /**
     * Log the method name from which m() is called.
     *
     * @param tag TAG to use in android log
     */
    public static void m(String tag) {
        Logger l = LoggerFactory.getLogger(tag);
        l.trace(Info.getMethodName(sDepth));
    }

    /**
     * Log the method name from which m() is called.
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     */
    public static void m(String tag, boolean debug) {
        if (debug) {
            Logger l = LoggerFactory.getLogger(tag);
            l.trace(Info.getMethodName(sDepth));
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
            Logger l = LoggerFactory.getLogger(tag);
            l.trace("{}, {}", Info.getMethodName(sDepth), msg);
        }
    }

    /**
     * Log the method name from which m() is called with the current thread information
     *
     * @param tag TAG to use in android log
     */
    public static void mt(String tag) {
        Logger l = LoggerFactory.getLogger(tag);
        l.trace("{}, {}", Info.getMethodName(sDepth), Info.getThreadInfoString());
    }

    /**
     * Log the method name from which m() is called with the current thread information
     *
     * @param tag   TAG to use in android log
     * @param debug Log only if debug is true
     */
    public static void mt(String tag, boolean debug) {
        if (debug) {
            Logger l = LoggerFactory.getLogger(tag);
            l.trace("{}, {}", Info.getMethodName(sDepth), Info.getThreadInfoString());
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
            Logger l = LoggerFactory.getLogger(tag);
            l.trace("{}, {}, {}", Info.getMethodName(sDepth), Info.getThreadInfoString(), msg);
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
        Logger l = LoggerFactory.getLogger(tag);
        l.trace("l: {}, d: {}", len, byteArrayToString(array, len));
    }
}
