package fr.coppernic.lib.utils.result

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.*

class ResultTest {

    @Test
    fun resultExceptionWithResult() {
        val e = ResultException(Result(RESULT.CANCELLED))
        assertThat(e.toString(), `is`<String>("fr.coppernic.lib.utils.result.ResultException, Result(result=CANCELLED, message=, cause=null)"))
        assertThat(e.result, `is`(Result(RESULT.CANCELLED)))
    }

    @Test
    fun fromErrno() {
        assertThat(RESULT.fromErrno(Errno.EBUSY), `is`(RESULT.BUSY))
    }

    @Test
    fun fromOrdinal() {
        assertThat(RESULT.fromOrdinal(1), `is`(RESULT.ERROR))
    }

    @Test
    fun toException() {
        val e = RESULT.ALREADY_OPENED.toException()
        assertThat(e.result, `is`(Result(RESULT.ALREADY_OPENED)))
        assertThat(e.toString(), `is`("fr.coppernic.lib.utils.result.ResultException, Result(result=ALREADY_OPENED, message=, cause=null)"))
    }

    @Test
    @Throws(IOException::class, ClassNotFoundException::class)
    fun serialization() {
        val resultOut = RESULT.ERROR
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(resultOut)

        val bytes = baos.toByteArray()

        val bais = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bais)
        val resultIn = ois.readObject() as RESULT

        assertThat(resultIn, `is`(RESULT.ERROR))
    }
}
