package fr.coppernic.lib.utils.os

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import javax.inject.Inject

class BatteryHelper @Inject constructor(private val context: Context) {

    var isCharging = false
    var isAc = false
    var isUsb = false
    var isWireless = false
    var isPlugged = false
    var batteryPct = 0f

    var temperature = -1
    var voltage = -1

    var batteryListener: BatteryListener = object : BatteryListener {
        override fun onBatteryPlugged() {
        }

        override fun onBatteryLow() {
        }
    }

    private val powerReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action ?: "") {
                Intent.ACTION_BATTERY_CHANGED -> updateBatteryStatus(intent)
                Intent.ACTION_POWER_CONNECTED -> batteryListener.onBatteryPlugged()
                Intent.ACTION_BATTERY_LOW -> batteryListener.onBatteryLow()
            }
        }
    }

    init {
        updateCurrentBatteryStatus()
    }

    fun register() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_POWER_CONNECTED)
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        filter.addAction(Intent.ACTION_BATTERY_OKAY)
        context.registerReceiver(powerReceiver, filter)
    }

    fun unregister() {
        context.unregisterReceiver(powerReceiver)
    }

    override fun toString(): String {
        return "CpcBattery{" +
                ", ac=" + isAc +
                ", usb=" + isUsb +
                ", pct=" + batteryPct * 100 + "%" +
                ", °C=" + temperature +
                ", VS=" + voltage +
                '}'.toString()
    }

    fun updateCurrentBatteryStatus() {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        if (batteryStatus != null) {
            updateBatteryStatus(batteryStatus)
        }
    }

    @Synchronized
    private fun updateBatteryStatus(batteryStatus: Intent) {
        // Are we charging / charged?
        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        val plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                -1)

        isPlugged = plugged > 0
        isAc = plugged == BatteryManager.BATTERY_PLUGGED_AC
        isUsb = plugged == BatteryManager.BATTERY_PLUGGED_USB
        // isWireless = plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        isWireless = plugged == 4 // Wireless

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        batteryPct = level / scale.toFloat()

        voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)


        if (isCharging) {
            batteryListener.onBatteryPlugged()
        }
    }

    interface BatteryListener {
        fun onBatteryPlugged()

        fun onBatteryLow()
    }
}