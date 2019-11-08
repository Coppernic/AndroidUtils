package fr.coppernic.lib.utils.debug

import org.slf4j.Logger
import java.io.PrintWriter
import java.io.StringWriter

object LogHelper {
    fun getStackTraceString(t: Throwable): String {
        // Don't replace this with Log.getStackTraceString() - it hides
        // UnknownHostException, which is not what we want.
        val sw = StringWriter(256)
        val pw = PrintWriter(sw, false)
        t.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}

fun Logger.trace(t: Throwable) {
    trace(LogHelper.getStackTraceString(t))
}

fun Logger.debug(t: Throwable) {
    trace(LogHelper.getStackTraceString(t))
}

fun Logger.info(t: Throwable) {
    trace(LogHelper.getStackTraceString(t))
}

fun Logger.warn(t: Throwable) {
    trace(LogHelper.getStackTraceString(t))
}

fun Logger.error(t: Throwable) {
    trace(LogHelper.getStackTraceString(t))
}
