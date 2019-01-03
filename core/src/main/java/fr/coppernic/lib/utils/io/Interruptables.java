package fr.coppernic.lib.utils.io;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Class to use Interruptables objects quietly
 */
public final class Interruptables {

    private Interruptables() {
    }

    /**
     * Acquire a sem permit quietly
     *
     * @param sem Semaphore
     */
    public static void acquireQuietly(Semaphore sem) {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Acquires quietly a permit from semaphore, if one becomes available within the
     * given waiting time and the current thread has not been interrupted.
     * <p>
     * Acquires a permit, if one is available and returns immediately, with the value
     * true, reducing the number of available permits by one.
     * </p>
     * <p>
     * If no permit is available then the current thread becomes disabled for thread
     * scheduling purposes and lies dormant until one of three things happens:
     * </p>
     * <p><ul>
     * <li>Some other thread invokes the release() method for this semaphore and the
     * current thread is next to be assigned a permit; or</li>
     * <li>Some other thread interrupts the current thread; or</li>
     * <li>The specified waiting time elapses.</li>
     * </ul></p>
     * <p>
     * If a permit is acquired then the value true is returned.
     * </p>
     * <p>
     * If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or</li>
     * <li>is interrupted while waiting to acquire a permit,</li>
     * </ul></p>
     * <p>
     * then it returns false.
     * </p>
     * <p>
     * If the specified waiting time elapses then the value false is returned. If the
     * time is less than or equal to zero, the method will not wait at all.
     * </p>
     *
     * @param sem     : Semaphore objects to get permits from
     * @param timeout :  the maximum time to wait for a permit
     * @param unit    : the time unit of the timeout argument
     * @return true if a permit was acquired and false if the waiting time elapsed
     * before a permit was acquired
     */
    public static boolean tryAcquireQuietly(Semaphore sem, long timeout, TimeUnit unit) {
        boolean ret = false;
        try {
            ret = sem.tryAcquire(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }
}
