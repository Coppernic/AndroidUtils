package fr.coppernic.lib.utils.os

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import fr.coppernic.lib.utils.io.StorageHelper
import timber.log.Timber
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

object MemoryHelper {

    /**
     * Calculates the free memory of the device. This is based on an inspection
     * of the filesystem, which in android
     * devices is stored in RAM.
     *
     * @return Number of bytes available.
     */
    fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        return availableBlocks * blockSize
    }

    /**
     * Calculates the total memory of the device. This is based on an inspection
     * of the filesystem, which in android
     * devices is stored in RAM.
     *
     * @return Total number of bytes.
     */
    fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        return totalBlocks * blockSize
    }

    /**
     * Get device's memory info.
     *
     *
     * Get ram information and storage information. Values are in kilo bit.
     *
     * @return Device's memory information
     */
    fun getMemInfo(): MemInfo {
        val ret = MemInfo()
        try {
            val reader = BufferedReader(FileReader(
                    "/proc/meminfo"))
            var line: String? = reader.readLine()
            if (line != null && line.contains("MemTotal")) {
                val lines = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                ret.ramTotal = java.lang.Long.parseLong(lines[1])
            }
            line = reader.readLine()
            if (line != null && line.contains("MemFree")) {
                val lines = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                ret.ramFree = java.lang.Long.parseLong(lines[1])
            }
            ret.ramUsed = ret.ramTotal - ret.ramFree
            reader.close()
        } catch (ignored: FileNotFoundException) {

        } catch (ignored: IOException) {

        }

        val list = StorageHelper.determineStorageOptions()
        Timber.d("Calculate size of %s", list[0])
        val stat = StatFs(list[0])
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            ret.memTotal = stat.blockSize.toLong() * stat.blockCount.toLong() shr 10
            ret.memFree = stat.blockSize.toLong() * stat.availableBlocks.toLong() shr 10
        } else {
            ret.memTotal = stat.blockSizeLong * stat.blockCountLong
            ret.memFree = stat.blockSizeLong * stat.availableBlocksLong
        }
        Timber.d(ret.toString())
        ret.memUsed = ret.memTotal - ret.memFree

        return ret
    }

    fun getAvailableMemory(context: Context): Long {
        val mi = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        return mi.availMem / 1024L
    }

}

data class MemInfo constructor(var ramTotal: Long = 0,
                               var ramFree: Long = 0,
                               var ramUsed: Long = 0,
                               var memTotal: Long = 0,
                               var memFree: Long = 0,
                               var memUsed: Long = 0)