package fr.coppernic.lib.utils.time

import fr.coppernic.lib.utils.robolectric.RobolectricTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

class WatchDogTest: RobolectricTest() {
    private lateinit var w: WatchDog
    private var timeout = AtomicInteger(0)

    @Before
    fun setUp() {
        timeout.set(0)
        w = WatchDog()
    }

    @After
    fun after() {
        w.dispose()
    }

    @Test
    fun set() {
        w.set({
            assertThat(it, equalTo(w))
            timeout.set(1)
        }, 500)

        assertThat(timeout.get(), equalTo(0))
        assertThat(w.isTimedOut(), equalTo(false))
        sleep(600)
        assertThat(timeout.get(), equalTo(1))
        assertThat(w.isTimedOut(), equalTo(true))
    }

    @Test
    fun clear() {
        w.set({
            assertThat(it, equalTo(w))
            timeout.set(1)
        }, 500)
        sleep(200)
        assertThat(w.isTimedOut(), equalTo(false))
        w.clear()
        sleep(400)
        assertThat(timeout.get(), equalTo(0))
        assertThat(w.isTimedOut(), equalTo(false))
    }

    @Test
    fun setOnce() {
        val listener: (WatchDog) -> Unit = {
            assertThat(it, equalTo(w))
            timeout.incrementAndGet()
        }
        w.set(listener, 400)
        w.set(listener, 410)
        w.set(listener, 420)
        w.set(listener, 430)
        assertThat(timeout.get(), equalTo(0))
        assertThat(w.isTimedOut(), equalTo(false))
        sleep(700)
        assertThat(timeout.get(), equalTo(1))
        assertThat(w.isTimedOut(), equalTo(true))
    }

}