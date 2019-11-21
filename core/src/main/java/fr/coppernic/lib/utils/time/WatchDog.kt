package fr.coppernic.lib.utils.time

import fr.coppernic.lib.utils.BuildConfig.DEBUG
import fr.coppernic.lib.utils.io.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class WatchDog @Inject constructor() : Disposable {

    private var timer: Timer = Timer()
    private var watchDogListener: (WatchDog) -> Unit = {}
    private val disposed = AtomicBoolean(false)
    private val isTimedOut = AtomicBoolean(false)
    private var timeout: TimerTask? = null

    @Synchronized
    fun clear() {
        timeout?.cancel()
    }

    fun set(listener: (WatchDog) -> Unit, time: Long, unit: TimeUnit): Boolean {
        return set(listener, unit.toMillis(time))
    }

    @Synchronized
    fun set(listener: (WatchDog) -> Unit, ms: Long): Boolean {
        if (isDisposed) {
            if (DEBUG) {
                throw RuntimeException("Timer is disposed, cannot execute set($listener, $ms)")
            } else {
                return false
            }
        }

        this.watchDogListener = listener
        isTimedOut.set(false)
        if (timeout != null) {
            clear()
        }
        timeout = Timeout()
        timer.schedule(timeout!!, ms)
        return true
    }

    fun isTimedOut(): Boolean {
        return isTimedOut.get()
    }

    @Synchronized
    override fun dispose() {
        disposed.set(true)
        clear()
        timer.cancel()
    }

    override fun isDisposed(): Boolean {
        return disposed.get()
    }

    private inner class Timeout : TimerTask() {
        override fun run() {
            isTimedOut.set(true)
            watchDogListener(this@WatchDog)
        }
    }
}

