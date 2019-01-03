package fr.coppernic.lib.utils.io

import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import fr.coppernic.lib.utils.BuildConfig.DEBUG
import timber.log.Timber
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
object StorageHelper {

    /**
     * Try getting real SDCard path.
     *
     *  Context is needed for devices running on Android 4.4 and superior.
     *  <p>
     *  Trying several method to get SD path :
     *  <li>ContextCompat.getExternalCacheDirs()</li>
     *  <li>tryGettingSdExternalStorage()</li>
     *  <li>Environment.getExternalStorageDirectory()</li>
     *  <li><Environment.getDataDirectory()/li>
     *  <li><Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)/li>
     *
     * @param c Android Context
     * @return SDCard path
     */
    fun tryGettingSdExternalStorage(c: Context): File? {
        // First we handle the case where we are on KitKat or superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val l = ContextCompat.getExternalCacheDirs(c)
            for (f in l) {
                if (f == null) {
                    continue
                }
                val path = f.path
                // SDCard path are formatted like /storage/ABCD-1234/...
                if (!path.startsWith("/storage/emulated")) {
                    val g = FileHelper.getShortestWritablePath(f)
                    if (g != null) {
                        return g
                    }
                }
            }
        }
        // Then we try the legacy manner
        val ret = tryGettingSdExternalStorage()
        //Finally we use the classic android api
        if (ret == null) {
            var f = Environment.getExternalStorageDirectory()
            if (f.canWrite()) {
                return f
            }

            f = Environment.getDataDirectory()
            if (f.canWrite()) {
                return f
            }

            f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (f.canWrite()) {
                return f
            }
        }
        return ret
    }
    
    /**
     * On Cone, there are two external storage. One in SD, another in internal storage.
     * We get the list of external storage and give the first one that begins by
     * "/storage"
     *
     * @return Path to external storage or null if not found
     */
    fun tryGettingSdExternalStorage(): File? {
        var ret: File? = null
        val list = determineStorageOptions()

        if (list.size == 0) {
            //nothing to do
        } else if (list.size == 1) {
            ret = File(list[0])
        } else {
            ret = File(list[0])
            for (s in list) {
                if (s.startsWith("/storage")) {
                    ret = File(s)
                    break
                }
            }
        }
        return ret
    }

    /**
     * @return The list of external storage available on the system.
     */
    fun determineStorageOptions(): ArrayList<String> {

        val mMounts = ArrayList<String>()
        val mVOld = ArrayList<String>()

        readMountsFile(mMounts)

        readVoldFile(mVOld)

        compareMountsWithVold(mMounts, mVOld)

        testAndCleanMountsList(mMounts)

        return mMounts
    }

    private fun readMountsFile(mMounts: ArrayList<String>) {

        /*
         * Scan the /proc/mounts file and look for lines like this:
         * /dev/block/vold/179:1 /mnt/sdcard vfat
         * rw,dirsync,nosuid,nodev,noexec,
         * relatime,uid=1000,gid=1015,fmask=0602,dmask
         * =0602,allow_utime=0020,codepage
         * =cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
         *
         * When one is found, split it into its elements and then pull out the
         * path to the that mount point and add it to the arraylist
         */

        // some mount files don't list the default
        // path first, so we add it here to
        // ensure that it is first in our list
        mMounts.add("/mnt/sdcard")

        try {
            val scanner = Scanner(File("/proc/mounts"))
            while (scanner.hasNext()) {
                val line = scanner.nextLine()
                if (line.startsWith("/dev/block/vold/")) {
                    val lineElements = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val element = lineElements[1]

                    // don't add the default mount path
                    // it's already in the list.
                    if (element != "/mnt/sdcard") {
                        mMounts.add(element)
                    }
                }
            }
            scanner.close()
        } catch (e: Exception) {
            // Auto-generated catch block
            e.printStackTrace()
        }

    }

    private fun readVoldFile(mVold: ArrayList<String>) {
        /*
         * Scan the /system/etc/vold.fstab file and look for lines like this:
         * dev_mount sdcard /mnt/sdcard 1
         * /devices/platform/s3c-sdhci.0/mmc_host/mmc0
         *
         * When one is found, split it into its elements and then pull out the
         * path to the that mount point and add it to the arraylist
         */

        // some devices are missing the vold file entirely
        // so we add a path here to make sure the list always
        // includes the path to the first sdcard, whether real
        // or emulated.
        mVold.add("/mnt/sdcard")

        try {
            val scanner: Scanner = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Scanner(File("/system/etc/vold.fstab"))
            } else {
                Scanner(File("/fstab.qcom"))
            }

            while (scanner.hasNext()) {
                val line = scanner.nextLine()
                if (line.startsWith("dev_mount")) {
                    val lineElements = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    var element = lineElements[2]

                    if (element.contains(":")) {
                        element = element.substring(0, element.indexOf(":"))
                    }

                    // don't add the default vold path
                    // it's already in the list.
                    if (element != "/mnt/sdcard") {
                        mVold.add(element)
                    }
                }
            }
            scanner.close()
        } catch (e: Exception) {
            Timber.w(e.toString())
        }

    }

    private fun compareMountsWithVold(mMounts: ArrayList<String>,
                                      mVold: ArrayList<String>) {
        /*
         * Sometimes the two lists of mount points will be different. We only
         * want those mount points that are in both list.
         *
         * Compare the two lists together and remove items that are not in both
         * lists.
         */

        var i = 0
        while (i < mMounts.size) {
            val mount = mMounts[i]
            if (!mVold.contains(mount)) {
                mMounts.removeAt(i--)
            }
            i++
        }

        // don't need this anymore, clear the vold list to reduce memory
        // use and to prepare it for the next time it's needed.
        mVold.clear()
    }

    private fun testAndCleanMountsList(mMounts: ArrayList<String>) {
        /*
         * Now that we have a cleaned list of mount paths Test each one to make
         * sure it's a valid and available path. If it is not, remove it from
         * the list.
         */

        var i = 0
        while (i < mMounts.size) {
            val mount = mMounts[i]
            val root = File(mount)
            if (!root.exists() || !root.isDirectory || !root.canWrite()) {
                if (DEBUG) {
                    Timber.d("$mount exists ${root.exists()}")
                    Timber.d("$mount isDirectory ${root.isDirectory}")
                    Timber.d("$mount canWrite ${root.canWrite()}")
                }
                mMounts.removeAt(i--)
            }
            i++
        }
    }

}