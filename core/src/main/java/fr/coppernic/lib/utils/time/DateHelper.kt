package fr.coppernic.lib.utils.time

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {

    /**
     * Get the current date string with format yyyyMMdd
     *
     * @return the date string
     */
    fun getCurrentDate(): String {
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("yyyyMMdd", Locale.US)
        return format.format(c.time)
    }

    /**
     * Get the current hour string with format kkmmss
     *
     * @return the hour string
     */
    fun getCurrentHour(): String {
        val c = Calendar.getInstance()
        val format = SimpleDateFormat("kkmmss", Locale.US)
        return format.format(c.time)
    }

    /**
     * Returns a string containing current date time formatted according to the parameters
     *
     * @param format Date time output format (ex: "yyyy-MM-dd HH:mm:ss")
     * @param locale Locale used (ex: Locale.France)
     * @return Formatted date time string
     */
    fun getCurrentDateTime(format: String, locale: Locale): String {
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat(format, locale)
        return sdf.format(c.time)
    }

    /**
     * Get the String date from timestamp (nb millis after 1970)
     *
     *
     * Default format is "yyyy-MM-dd HH:mm:ss"
     *
     *
     * @param milli nb millis after 1970
     * @return Date string
     */
    fun timestampToDate(milli: Long): String {
        return timestampToDate(milli, "yyyy-MM-dd HH:mm:ss")
    }

    /**
     * Get the String date from timestamp (nb millis after 1970)
     *
     * @param milli  nb millis after 1970
     * @param format Date format
     * @return Date string
     */
    fun timestampToDate(milli: Long, format: String): String {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milli
        val sdf = SimpleDateFormat(format, Locale.US)
        val date = calendar.time
        return sdf.format(date)
    }
}