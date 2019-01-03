package fr.coppernic.lib.utils.debug;

import java.util.HashMap;

import timber.log.Timber;

/**
 * Code profiling class
 * Created by bastien on 07/12/15.
 */
public class SimpleProfiler {

    private static final HashMap<String, Long> mMapTime = new HashMap<String, Long>();
    private static long mBeginTime = 0;

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
        Timber.d("[Profiler] : Spent %d ms in %s", end - mBeginTime, s);
    }

    /**
     * Start measuring time from this point.
     *
     * @param b   If false, this method does nothing.
     * @param tag String to store the time value in a map
     */
    public static synchronized void begin(boolean b, String tag) {
        if (b) {
            mMapTime.put(tag, System.currentTimeMillis());
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
                Timber.d("[Profiler] : Spent %d ms in %s", end - mMapTime.get(tag), tag);
            } catch (Exception e) {
                Timber.d(e.toString());
                Timber.d("Is key " + tag + " exists ? " + mMapTime.containsKey(tag));
            }
        }
    }
}
