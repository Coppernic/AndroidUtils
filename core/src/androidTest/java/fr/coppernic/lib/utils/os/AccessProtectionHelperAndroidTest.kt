package fr.coppernic.lib.utils.os

import android.content.Context
import android.util.Pair
import androidx.test.core.app.ApplicationProvider
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import timber.log.Timber

class AccessProtectionHelperAndroidTest {

    private lateinit var accessProtectionHelper: AccessProtectionHelper
    private lateinit var context: Context

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
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun notAllowed() {
        accessProtectionHelper = AccessProtectionHelper(context, HashSet())
        assertThat(accessProtectionHelper.isPackageAllowed(context.packageName), equalTo(false))
    }

    //748D82E5C019B361C7AD2C6F6AB8587602062EF4F5D444E3A2E44F2D8DCBFAC5
    //E6D539D9E495619B32D32C7CFFB1FEAE1EFA86726CE6F23668E4FB6D1D80F122
    @Test
    fun allowed() {

        accessProtectionHelper = AccessProtectionHelper(context,
                HashSet(listOf(
                        Pair("fr.coppernic.lib.utils.test", "E6D539D9E495619B32D32C7CFFB1FEAE1EFA86726CE6F23668E4FB6D1D80F122".toLowerCase())
                )))
        assertThat(accessProtectionHelper.isPackageAllowed(context.packageName), equalTo(true))
    }
}
