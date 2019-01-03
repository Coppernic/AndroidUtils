package fr.coppernic.lib.utils.result

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.*

class ResultTest {

    @Test
    fun result() {
        val result = RESULT.OK
        assertThat(result.message, equalTo(""))
        assertThat(result.cause, nullValue())
        assertThat(result.toLogString(), equalTo("OK"))
    }

    @Test
    fun resultMessageCause() {
        val result = RESULT.ERROR.withMessage("message").withCause(Throwable())
        assertThat(result.message, equalTo("message"))
        assertThat(result.cause, notNullValue())
        assertThat(result.toLogString(), equalTo("ERROR, message, cause: java.lang.Throwable"))
    }

    @Test
    fun resultExceptionWithResult() {
        val e = ResultException(RESULT.CANCELLED)
        assertThat(e.toString(), `is`<String>("ResultException: CANCELLED"))
        assertThat(e.result, `is`(RESULT.CANCELLED))
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
        assertThat(e.result, `is`(RESULT.ALREADY_OPENED))
        assertThat(e.toString(), `is`("ResultException: ALREADY_OPENED"))
    }

    @Test
    @Throws(IOException::class, ClassNotFoundException::class)
    fun serialization() {
        val t = Throwable("message")
        val resultOut = RESULT.ERROR.withCause(t)
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(resultOut)

        val bytes = baos.toByteArray()

        val bais = ByteArrayInputStream(bytes)
        val ois = ObjectInputStream(bais)
        val resultIn = ois.readObject() as RESULT

        assertThat(resultIn, `is`(RESULT.ERROR))
        assertThat(resultIn.cause, `is`(notNullValue()))
        assertThat(resultIn.cause?.message, `is`("message"))
        resultIn.cause?.printStackTrace()
    }
}
