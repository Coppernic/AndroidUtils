package fr.coppernic.lib.utils.net

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import fr.coppernic.lib.utils.BuildConfig.DEBUG
import fr.coppernic.lib.utils.result.RESULT
import timber.log.Timber

@SuppressLint("MissingPermission")
object WifiHelper {

    fun deleteAllConfigs(context: Context): RESULT {
        var res = RESULT.OK
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        try {
            val listConf = manager.configuredNetworks
            // A NPE can occur here
            for (conf in listConf) {
                if (DEBUG) {
                    Timber.v("Remove network %s", conf.SSID)
                }
                res = if (!manager.removeNetwork(conf.networkId)) {
                    RESULT.ERROR
                } else {
                    RESULT.OK
                }
            }
        } catch (e: Exception) {
            Timber.e("Exception in deleteAllConfigs : $e")
            e.printStackTrace()
            res = RESULT.ERROR
        }

        return res
    }

    fun enableWifi(context: Context, enable: Boolean) {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (manager.isWifiEnabled != enable) {
            manager.isWifiEnabled = enable
        }
    }

    fun addWifiWpaPsk(context: Context, ssid: String, pass: String): RESULT {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var res = RESULT.OK

        if (isNetworkInList(context, ssid)) {
            Timber.w("SSID already present : $ssid")
            res = RESULT.ALREADY_SET
        } else {
            val wc = WifiConfiguration()
            wc.SSID = ssid
            wc.preSharedKey = pass
            wc.status = WifiConfiguration.Status.ENABLED
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA)

            val id = manager.addNetwork(wc)
            if (id == -1) {
                Timber.e("Add network failed : %s", wc.toString())
                res = RESULT.ERROR
            } else if (!manager.saveConfiguration()) {
                Timber.e("Save config failed")
                res = RESULT.ERROR
            }
        }
        return res
    }

    /**
     * Tell if SSID is already configured.
     *
     *
     *
     * This method is not working when ethernet is enabled on Android 4.4 and above.
     *
     * @param ssid NAme of Wifi network
     * @return true if SSID is already configured, false otherwise
     */
    fun isNetworkInList(context: Context, ssid: String): Boolean {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var ret = false
        val listConf = manager.configuredNetworks
        //List conf is null on 4.4 if ethernet is enabled
        if (listConf != null) {
            for (wc in listConf) {
                if (ssid == wc.SSID) {
                    ret = true
                    break
                }
            }
        }
        return ret
    }

    fun enableNetwork(context: Context, enable: Boolean, ssid: String): RESULT {
        val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var res = RESULT.OK
        var id = -1

        val listConf = manager.configuredNetworks

        if (listConf != null) {
            for (conf in listConf) {
                if (conf?.SSID == ssid) {
                    id = conf.networkId
                }
            }
        }

        if (id != -1) {
            if (enable && !manager.enableNetwork(id, true)) {
                Timber.e("Enable network $id Failed : $ssid")
                res = RESULT.ERROR
            } else if (!enable && !manager.disableNetwork(id)) {
                Timber.e("Disable network $id Failed : $ssid")
                res = RESULT.ERROR
            } else if (!manager.saveConfiguration()) {
                Timber.e("Save config failed")
                res = RESULT.ERROR
            }
        } else {
            res = RESULT.ERROR
        }

        return res
    }
}
