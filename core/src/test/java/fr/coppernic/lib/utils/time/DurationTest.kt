package fr.coppernic.lib.utils.time

import junit.framework.Assert.assertEquals
import org.junit.Test

class DurationTest {

    @Test
    fun testDuration() {
        val duration = Duration(0)
        duration.end = 1
        assertEquals("00 00:00:00", duration.getDurationString())
        duration.end = 1000
        assertEquals("00 00:00:01", duration.getDurationString())
        duration.end = 60000
        assertEquals("00 00:01:00", duration.getDurationString())
        duration.end = 3600000
        assertEquals("00 01:00:00", duration.getDurationString())
        duration.end = 86400000
        assertEquals("01 00:00:00", duration.getDurationString())
    }
}