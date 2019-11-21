package fr.coppernic.lib.test.rules

import fr.bipi.tressence.common.utils.Info
import fr.bipi.tressence.console.SystemLogTree
import org.junit.*
import timber.log.Timber

class ThreadRuleTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            Timber.plant(SystemLogTree())
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            Timber.uprootAll()
        }
    }

    @Rule
    @JvmField
    val threadRule = ThreadRule()

    @Before
    fun setUp() {
        Timber.d("${Info.getMethodName()}: I am on thread ${Info.getThreadInfoString()}")
    }

    @Test
    fun thread() {
        Timber.d("${Info.getMethodName()}: I am on thread ${Info.getThreadInfoString()}")
    }
}
