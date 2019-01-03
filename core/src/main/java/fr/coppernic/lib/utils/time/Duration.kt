package fr.coppernic.lib.utils.time

import java.util.*
import javax.inject.Inject

data class Duration @Inject constructor(val start: Long = System.currentTimeMillis()) {
    var diff: Long = 0
    var milli: Long = 0
    var sec: Long = 0
    var min: Long = 0
    var hours: Long = 0
    var days: Long = 0
    var totalSec: Long = 0
    var totalMin: Long = 0
    var totalHours: Long = 0

    var end: Long = 0
        set(value) {
            field = value
            computeDuration()
        }

    constructor(start: Long, end: Long) : this(start) {
        this.end = end
    }

    /**
     * Set end value as currentTimeMillis() and compute duration
     */
    fun setEnd() {
        end = System.currentTimeMillis()
    }

    /**
     * Get duration string with format dd kk:mm:ss
     *
     * @return duration string
     */
    fun getDurationString(): String {
        return String.format(Locale.US, "%02d %02d:%02d:%02d", days, hours, min, sec)
    }


    private fun computeDuration() {
        diff = end - start
        milli = diff % 1000

        totalSec = diff / 1000
        sec = totalSec % 60

        totalMin = diff / (1000 * 60)
        min = totalMin % 60

        totalHours = diff / (1000 * 60 * 60)
        hours = totalHours % 24

        days = diff / (1000 * 60 * 60 * 24)
    }
}