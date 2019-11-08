package fr.coppernic.lib.utils.debug;

import java.util.HashMap;

import static fr.coppernic.lib.utils.debug.InternalLog.LOGGER;

/**
 * Code profiling class
 * Created by bastien on 07/12/15.
 */
public final class SimpleProfiler {

    private static final HashMap<String, Long> M_MAP_TIME = new HashMap<>();
    private static long mBeginTime = 0;

    private SimpleProfiler() {
    }

    /**
     * Start measuring time from this point. Each call of #begin() reset the
     * value.
     */
    public static synchronized void begin() {
        mBeginTime = System.currentTimeMillis();
    }

    /**
     * Log the time measured between #begin() and #end(String).
     *
     * @param s String identifier for log purpose
     */
    public static synchronized void end(String s) {
        long end = System.currentTimeMillis();
        LOGGER.debug("[Profiler] : Spent {} ms in {}", end - mBeginTime, s);
    }

    /**
     * Start measuring time from this point.
     *
     * @param b   If false, this method does nothing.
     * @param tag String to store the time value in a map
     */
    public static synchronized void begin(boolean b, String tag) {
        if (b) {
            M_MAP_TIME.put(tag, System.currentTimeMillis());
        }
    }

    /**
     * End measuring time from this point. Print a log with the difference with #begin
     * (tag)
     *
     * @param b   if false, this method does nothing
     * @param tag String to get the begin's value and print a log
     */
    public static synchronized void end(boolean b, String tag) {
        if (b) {
            long end = System.currentTimeMillis();
            try {
                //noinspection ConstantConditions
                LOGGER.debug("[Profiler] : Spent {} ms in {}", end - M_MAP_TIME.get(tag), tag);
            } catch (Exception e) {
                LOGGER.debug(e.toString());
                LOGGER.debug("Is key " + tag + " exists ? " + M_MAP_TIME.containsKey(tag));
            }
        }
    }
}
