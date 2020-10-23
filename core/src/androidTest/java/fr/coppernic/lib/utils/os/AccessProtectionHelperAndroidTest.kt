package fr.coppernic.lib.utils.os

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import fr.coppernic.lib.utils.log.LogDefines
import org.amshove.kluent.shouldBeTrue
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
        LogDefines.setVerbose(true)
    }

    @Test
    fun notAllowed() {
        accessProtectionHelper = AccessProtectionHelper(context, mapOf(
                "c8a2e9bccf597c2fb6dc66bee293fc13f2fc47ec77bc6b2b0d52c11f51192ab8" to setOf("NOPE".toRegex())
        ))
        assertThat(accessProtectionHelper.arePackagesAllowed(setOf(context.packageName)), equalTo(false))
        assertThat(accessProtectionHelper.arePackagesAllowed(setOf("com.android.camera")), equalTo(false))
    }

    //748D82E5C019B361C7AD2C6F6AB8587602062EF4F5D444E3A2E44F2D8DCBFAC5
    //E6D539D9E495619B32D32C7CFFB1FEAE1EFA86726CE6F23668E4FB6D1D80F122
    @Test
    fun allowed() {
        accessProtectionHelper = AccessProtectionHelper(context,
                mapOf("E6D539D9E495619B32D32C7CFFB1FEAE1EFA86726CE6F23668E4FB6D1D80F122".toLowerCase() to setOf("fr.coppernic.lib.utils.test".toRegex()))
        )

        accessProtectionHelper.arePackagesAllowed(setOf(context.packageName)).shouldBeTrue()
    }

    @Test
    fun allowedSeveral() {
        accessProtectionHelper = AccessProtectionHelper(context,
                mapOf(
                        "E6D539D9E495619B32D32C7CFFB1FEAE1EFA86726CE6F23668E4FB6D1D80F122".toLowerCase()
                                to setOf(
                                "or.example.com".toRegex(),
                                "prout.com".toRegex(),
                                "fr.coppernic.lib.utils.test".toRegex()),
                        "748D82E5C019B361C7AD2C6F6AB8587602062EF4F5D444E3A2E44F2D8DCBFAC5"
                                to setOf("fr.coppernic.lib.utils.test".toRegex())
                )
        )
        accessProtectionHelper.arePackagesAllowed(setOf(context.packageName)).shouldBeTrue()
    }
}
