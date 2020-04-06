package fr.coppernic.lib.utils.os

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import timber.log.Timber

class AccessProtectionHelperAndroidTest {

    lateinit var accessProtectionHelper: AccessProtectionHelper
    lateinit var context: Context

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            Timber.plant(Timber.DebugTree())
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            Timber.uprootAll()
        }
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        accessProtectionHelper = AccessProtectionHelper(context)
    }

    @Test
    fun notAllowed() {
        assertThat(accessProtectionHelper.isPackageAllowed(context.packageName), equalTo(false))
    }

    //748D82E5C019B361C7AD2C6F6AB8587602062EF4F5D444E3A2E44F2D8DCBFAC5
    @Test
    fun allowed() {
        accessProtectionHelper.whitelist["fr.coppernic.lib.utils.test"] = "748d82e5c019b361c7ad2c6f6ab8587602062ef4f5d444e3a2e44f2d8dcbfac5"
        assertThat(accessProtectionHelper.isPackageAllowed(context.packageName), equalTo(true))
    }
}
