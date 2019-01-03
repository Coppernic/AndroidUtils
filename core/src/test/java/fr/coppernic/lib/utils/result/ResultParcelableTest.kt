package fr.coppernic.lib.utils.result

import android.os.Parcel
import fr.coppernic.lib.utils.robolectric.RobolectricTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class ResultParcelableTest : RobolectricTest() {

    @Test
    fun parcelable() {
        val t = Throwable("message")
        val resultOut = RESULT.ERROR.withCause(t)
        val cpcResultOut = ResultParcelable(resultOut)

        val parcel = Parcel.obtain()
        cpcResultOut.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val cpcResultIn = ResultParcelable.createFromParcel(parcel)
        val resultIn = cpcResultIn.result
        MatcherAssert.assertThat(resultIn, CoreMatchers.`is`(resultOut))
        MatcherAssert.assertThat(resultIn.cause, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        MatcherAssert.assertThat(resultIn.cause?.message, CoreMatchers.`is`("message"))
    }

}