package fr.coppernic.lib.utils.net

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.net.ConnectivityManager
import android.net.Uri
import fr.coppernic.lib.utils.BuildConfig.DEBUG
import fr.coppernic.lib.utils.io.BytesHelper
import fr.coppernic.lib.utils.result.RESULT
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

const val APN_NAME = "name"
const val APN_APN = "apn"
const val APN_MCC = "mcc"
const val APN_MNC = "mnc"
const val APN_NUMERIC = "numeric"
const val MOBILE_NETWORK_LTE_WCDMA = 12
const val MOBILE_NETWORK_GSM_WCDMA_LTE = 9
const val MOBILE_NETWORK_GSM_WCDMA_AUTO = 3
const val MOBILE_NETWORK_WCDMA_ONLY = 2
const val MOBILE_NETWORK_GSM_ONLY = 1
const val MOBILE_NETWORK_GSM_WCDMA_PREF = 0
const val KEY_MOBILE_NETWORK_MODE = "preferred_network_mode"
private val APN_TABLE_URI = Uri.parse("content://telephony/carriers")
private val PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn")
private const val APN_TYPE = "type"
private const val APN_CURRENT = "current"
private const val APN_APN_ID = "apn_id"

@SuppressLint("MissingPermission")
object NetHelper {

    /**
     * Ping provided url
     *
     * @param url Url to ping
     * @return true if success, false in case of failure
     */
    fun ping(url: String): Boolean {
        return ping(url, 4)
    }

    /**
     * Ping provided url
     *
     * @param url   Url to ping
     * @param count -c option of ping utility
     * @return true if success, false in case of failure
     */
    fun ping(url: String, count: Int): Boolean {
        var str = ""
        try {
            val process = Runtime.getRuntime().exec("/system/bin/ping -c $count $url")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var i: Int
            val buffer = CharArray(4096)
            val output = StringBuilder()

            i = reader.read(buffer)
            while (i > 0) {
                output.append(buffer, 0, i)
                i = reader.read(buffer)
            }
            reader.close()

            str = output.toString()
            if (DEBUG) {
                Timber.v("Ret : $str")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return !(str == "" || str.contains(" 0 received"))
    }


    /**
     * Enable mobile data
     *
     * @param context Android context
     * @param enabled , true to enable, false to disable.
     * @throws NetException NetException
     */
    fun setMobileDataEnabled(context: Context, enabled: Boolean) {
        try {
            val conman = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val conmanClass = Class.forName(conman.javaClass.name)
            val connectivityManagerField = conmanClass.getDeclaredField("mService")
            connectivityManagerField.isAccessible = true
            val connectivityManager = connectivityManagerField.get(conman)
            val connectivityManagerClass = Class.forName(connectivityManager.javaClass.name)
            val setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", java.lang.Boolean.TYPE)
            setMobileDataEnabledMethod.isAccessible = true

            setMobileDataEnabledMethod.invoke(connectivityManager, enabled)
        } catch (e: Exception) {
            throw NetException(e)
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0...
     * @return mac address or empty string
     */
    fun getMACAddress(interfaceName: String): String {
        var ret = ""
        if (interfaceName.contentEquals("")) {
            ret = ""
        } else {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    if (DEBUG) {
                        Timber.d(intf.toString())
                    }
                    if (!intf.name.equals(interfaceName, ignoreCase = true)) {
                        continue
                    }
                    ret = getMacFromIntf(intf)
                }
            } catch (e: SocketException) {
                e.printStackTrace()
            }

        }
        return ret
    }

    /**
     * @param intf Network interface
     * @return Mac string
     * @throws SocketException
     */
    @Throws(SocketException::class)
    private fun getMacFromIntf(intf: NetworkInterface): String {
        var ret = ""
        val mac = intf.hardwareAddress
        if (mac == null) {
            //let's return ""
        } else {
            ret = BytesHelper.byteArrayToString(mac, mac.size, ":")
        }
        return ret
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    fun getIPAddress(useIPv4: Boolean): String {
        val ret = ""
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addresses = Collections.list(intf.inetAddresses)
                for (addr in addresses) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress.toUpperCase(Locale.US)
                        if (useIPv4) {
                            if (addr is Inet4Address) {
                                return sAddr
                            }
                        } else {
                            if (addr is Inet6Address) {
                                val token = sAddr.indexOf('%') // drop ip6 port suffix
                                return if (token < 0) sAddr else sAddr.substring(0, token)
                            }
                        }
                    }
                }
            }
        } catch (ignored: SocketException) {
        }

        return ret
    }

    /**
     * Insert an APN or update if existing.
     *
     * @param ctx  Android Context
     * @param name APN name
     * @param apn  APN apn
     * @param mcc  APN mcc
     * @param mnc  APN mnc
     * @return return the new APN id
     */
    fun insertApn(ctx: Context, name: String, apn: String, mcc: String,
                  mnc: String): Int {
        val c = getApn(ctx, name, apn)
        return if (c != null && c.count > 0) {
            updateApn(ctx, name, apn, mcc, mnc)
            c.moveToFirst()
            val id = c.getInt(c.getColumnIndex("_id"))
            c.close()
            id
        } else {
            insertApnInternal(ctx, name, apn, mcc, mnc)
        }
    }

    /**
     * Insert an APN.
     *
     * @param ctx  Android Context
     * @param name APN name
     * @param apn  APN apn
     * @param mcc  APN mcc
     * @param mnc  APN mnc
     * @return return the new APN id
     */
    private fun insertApnInternal(ctx: Context, name: String, apn: String, mcc: String,
                                  mnc: String): Int {
        var id = -1
        val resolver = ctx.contentResolver

        val values = ContentValues()
        values.put(APN_NAME, name)
        values.put(APN_APN, apn)
        values.put(APN_MCC, mcc)
        values.put(APN_MNC, mnc)
        values.put(APN_NUMERIC, mcc + mnc)
        values.put(APN_TYPE, "default,supl")
        values.put(APN_CURRENT, "1")

        var c: Cursor? = null
        try {
            val newRow = resolver.insert(APN_TABLE_URI, values)
            if (newRow != null) {
                c = resolver.query(newRow, null, null, null, null)
                if (c != null && c.moveToFirst()) {
                    val index = c.getColumnIndex("_id")
                    id = c.getShort(index).toInt()
                    if (DEBUG) {
                        Timber.d("Newly added APN : $id")
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        c?.close()
        return id
    }

    /**
     * Update an existing APN row
     *
     * @param ctx  Android Context
     * @param name APN name
     * @param apn  APN apn
     * @param mcc  APN mcc
     * @param mnc  APN mnc
     * @return OK or an ERROR
     */
    private fun updateApn(ctx: Context, name: String, apn: String, mcc: String,
                          mnc: String): RESULT {
        val resolver = ctx.contentResolver

        val values = ContentValues()
        values.put(APN_NAME, name)
        values.put(APN_APN, apn)
        values.put(APN_MCC, mcc)
        values.put(APN_MNC, mnc)
        values.put(APN_NUMERIC, mcc + mnc)
        values.put(APN_TYPE, "default,supl")
        values.put(APN_CURRENT, "1")

        val where = "$APN_NAME = ? AND $APN_APN = ?"
        val wArgs = arrayOf(name, apn)
        val nRowUpdated = resolver.update(APN_TABLE_URI, values, where, wArgs)
        return if (nRowUpdated > 0) {
            RESULT.OK
        } else {
            RESULT.ERROR
        }
    }

    /**
     * Delete apn row
     *
     * @param ctx  Android Context
     * @param name APN name
     * @param apn  APN apn
     * @param mcc  APN mcc
     * @param mnc  APN mnc
     * @return OK or an ERROR
     */
    fun deleteApn(ctx: Context, name: String, apn: String, mcc: String,
                  mnc: String): Int {
        val resolver = ctx.contentResolver

        val where = APN_NAME + "= ? AND " + APN_APN + " = ? AND " + APN_MCC + "= ? AND " +
                APN_MNC + "= ?"
        val args = arrayOf(name, apn, mcc, mnc)
        return resolver.delete(APN_TABLE_URI, where, args)
    }

    /**
     * Set the APN id as default APN
     *
     * @param ctx Android context
     * @param id  APN id
     * @return RESULT
     */
    fun setDefaultAPN(ctx: Context, id: Int): RESULT {
        var res = RESULT.ERROR
        val resolver = ctx.contentResolver
        //Code from intragate
        val values = ContentValues()
        values.put(APN_APN_ID, id)
        try {
            resolver.update(PREFERRED_APN_URI, values, null, null)
            val c = resolver.query(
                    PREFERRED_APN_URI,
                    arrayOf(APN_NAME, APN_NAME),
                    "_id=$id", null, null)
            if (c != null) {
                res = RESULT.OK
                c.close()
            } else {
                if (DEBUG) {
                    Timber.v("c is null")
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return res
    }

    fun getDefaultApn(ctx: Context): Cursor? {
        val resolver = ctx.contentResolver
        return resolver.query(PREFERRED_APN_URI, null, null, null, null)
    }

    fun getCurrentApn(ctx: Context): Cursor? {
        val resolver = ctx.contentResolver
        val columns = arrayOf("_id", APN_NAME, APN_APN, APN_MCC, APN_NUMERIC, APN_TYPE, APN_CURRENT)
        val where = "$APN_CURRENT = ? "
        val wArgs = arrayOf("" + 1)
        return resolver.query(APN_TABLE_URI, columns, where, wArgs, null)
    }

    fun getListOfApn(ctx: Context): Cursor? {
        val resolver = ctx.contentResolver
        return resolver.query(APN_TABLE_URI, null, null, null, null)
    }

    /**
     * Get a existing apn in a cursor
     *
     * @param ctx  Android Context
     * @param name APN name
     * @param apn  APN apn
     * @return Cursor
     */
    fun getApn(ctx: Context, name: String, apn: String): Cursor? {
        val resolver = ctx.contentResolver
        val columns = arrayOf("_id", APN_NAME, APN_APN, APN_MCC, APN_NUMERIC, APN_TYPE, APN_CURRENT)
        val where = "$APN_NAME = ? AND $APN_APN = ?"
        val wArgs = arrayOf(name, apn)
        return resolver.query(APN_TABLE_URI, columns, where, wArgs, null)
    }

    fun getApn(ctx: Context, id: Int): Cursor? {
        val resolver = ctx.contentResolver
        val columns = arrayOf("_id", APN_NAME, APN_APN, APN_MCC, APN_NUMERIC, APN_TYPE, APN_CURRENT)
        val where = "_id" + " = ? "
        val wArgs = arrayOf("" + id)
        return resolver.query(APN_TABLE_URI, columns, where, wArgs, null)
    }

    /**
     * isOnline - Check if there is a NetworkConnection
     *
     * @return boolean
     */
    fun isConnected(ctx: Context): Boolean {
        val cm = ctx.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    /**
     * Check is there is a wifi connectivity
     *
     * @param ctx Android context
     * @return true if a wifi connectivity exists
     */
    fun isWifiConnected(ctx: Context): Boolean {
        val cm = ctx.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        return netInfo != null && netInfo.isConnected
    }

    /**
     * Check is there is a ethernet connectivity
     *
     * @param ctx Android context
     * @return true if a ethernet connectivity exists
     */
    fun isEthernetConnected(ctx: Context): Boolean {
        val cm = ctx.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
        return netInfo != null && netInfo.isConnected
    }
}
