package fr.coppernic.lib.utils.os

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.Looper
import android.os.PowerManager
import android.support.annotation.RequiresApi
import fr.coppernic.lib.utils.BuildConfig
import fr.coppernic.lib.utils.core.HashHelpers
import fr.coppernic.lib.utils.io.BytesHelper
import fr.coppernic.lib.utils.result.RESULT
import timber.log.Timber


const val UID_SYSTEM = 1000

@Suppress("MemberVisibilityCanBePrivate")
object AppHelper {
    private val DEBUG = BuildConfig.DEBUG

    /**
     * Return the unique id of the context's package
     *
     * @param context Context
     * @return UID
     */
    fun getPackageUid(context: Context): Int {
        var ret = 0
        val packageName = context.packageName
        val pm = context.packageManager
        try {
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_GIDS)
            ret = pi.applicationInfo.uid
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return ret
    }

    /**
     * Return true if the app is a system one
     *
     * @param context Context
     * @return true if system, false otherwise
     */
    fun isSharingSystemUid(context: Context): Boolean {
        return UID_SYSTEM == getPackageUid(context)
    }

    /**
     * @return true if the current thread is the UI one
     */
    fun isUiThread(): Boolean {
        return Looper.getMainLooper() == Looper.myLooper()
    }

    /**
     * Tell is an app is installed
     *
     * @param ctx         Context
     * @param packageName app's package name
     * @return true if app is installed, false otherwise
     */
    fun isPackageInstalled(ctx: Context, packageName: String): Boolean {
        val pm = ctx.packageManager
        return try {
            val info = pm.getPackageInfo(packageName, 0)
            info != null
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get first package found according to regex provided in input
     *
     * @param ctx     Context
     * @param pattern Regex pattern of the package to find
     * @return Package name found of empty string
     */
    fun getFirstInstalledPackageWithPattern(ctx: Context, pattern: String): String {
        val pm = ctx.packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
        for (info in packages) {
            if (info.packageName.matches(pattern.toRegex())) {
                return info.packageName
            }
        }
        return ""
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    fun getAppVersionName(context: Context, packageName: String = context.packageName): String {
        if (packageName.trim().isEmpty()) return ""
        return try {
            context.packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }

    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    fun getAppVersionCode(context: Context, packageName: String = context.packageName): Int {
        if (packageName.trim().isEmpty()) return -1
        return try {
            context.packageManager.getPackageInfo(packageName, 0).versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }

    }

    /**
     * Return the application's signature.
     *
     * @param packageName The name of the package.
     * @return the application's signature
     */
    @SuppressLint("PackageManagerGetSignatures")
    fun getAppSignature(context: Context, packageName: String = context.packageName): List<Signature> {
        if (packageName.trim().isEmpty()) return emptyList()
        return try {
            val pm = context.packageManager
            pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures.toList()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getAppSignatureHash(context: Context, packageName: String, algorithm: String): String {
        if (packageName.trim().isEmpty()) return ""
        val signature = getAppSignature(context, packageName)
        return if (signature.isEmpty()) "" else BytesHelper.byteArrayToString(HashHelpers.hashTemplate(signature[0].toByteArray(), algorithm))
    }

    /**
     * Return the application's signature for SHA1 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for SHA1 value
     */
    fun getAppSignatureSHA1(context: Context, packageName: String = context.packageName): String {
        return getAppSignatureHash(context, packageName, "SHA1")
    }

    /**
     * Return the application's signature for SHA256 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for SHA256 value
     */
    fun getAppSignatureSHA256(context: Context, packageName: String = context.packageName): String {
        return getAppSignatureHash(context, packageName, "SHA256")
    }

    /**
     * Return the application's signature for MD5 value.
     *
     * @param packageName The name of the package.
     * @return the application's signature for MD5 value
     */
    fun getAppSignatureMD5(context: Context, packageName: String = context.packageName): String {
        return getAppSignatureHash(context, packageName, "MD5")
    }

    /**
     * Reboot device
     *
     * @param ctx Context
     */
    fun reboot(ctx: Context) {
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager?
        pm?.reboot(null)
    }

    /**
     * Shutdown device
     *
     * @param ctx Context
     */
    fun shutdown(ctx: Context) {
        ctx.startActivity(IntentHelper.getShutdownIntent())
    }

    /**
     * Tell if a service is currently running
     *
     * @param context      Context
     * @param serviceClass Service class
     * @return true if the service is running
     */
    @Deprecated(message = "As of {@link android.os.Build.VERSION_CODES#O}, this method\n" +
            "is no longer available to third party applications.  For backwards compatibility,\n" +
            "it will still return the caller's own services.")
    fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    /**
     * Kill background process
     *
     * Needs KILL_BACKGROUND_PROCESSES permission
     *
     * @param context Android context
     * @param pack    App package to kill
     * @return RESULT.OK or RESULT.NOT_FOUND
     */
    @SuppressLint("MissingPermission")
    fun killApp(context: Context, pack: String): RESULT {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processes = am.runningAppProcesses
        for (info in processes) {
            if (DEBUG) {
                Timber.v("$pack vs ${info.processName}")
            }
            if (info.processName == pack) {
                Timber.i("Killing %s", info.processName)
                am.killBackgroundProcesses(info.processName)
                return RESULT.OK
            }
        }
        return RESULT.NOT_FOUND
    }

    /**
     * Get an activity info of a launchable activity from app's name
     *
     * @param context Android context
     * @param appName Application's name
     * @return Activity info or null if not found
     */
    fun appNameToLaunchAbleActivityInfo(context: Context, appName: String): ActivityInfo? {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfos = pm.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        for (info in resolveInfos) {
            if (info != null && info.loadLabel(pm) == appName) {
                return info.activityInfo
            }
        }
        return null
    }

    /**
     * Helper to send a broadcast when app can be system.
     *
     * Main goal is to suppress a warning in logcat
     *
     * @param context Android context
     * @param intent  Intent to broadcast
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingPermission")
    fun sendBroadcast(context: Context, intent: Intent) {
        if (isSharingSystemUid(context)) {
            // Permissions android.permission.INTERACT_ACROSS_USERS shall be hold by app
            context.sendBroadcastAsUser(intent, android.os.Process.myUserHandle())
        } else {
            context.sendBroadcast(intent)
        }
    }

    /**
     * Helper to send a broadcast when app can be system.
     *
     *
     * Main goal is to suppress a warning in logcat
     *
     * @param context Android context
     * @param intent  Intent to broadcast
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingPermission")
    fun sendBroadcast(context: Context, intent: Intent, permission: String?) {
        if (isSharingSystemUid(context)) {
            // Permissions android.permission.INTERACT_ACROSS_USERS shall be hold by app
            context.sendBroadcastAsUser(intent, android.os.Process.myUserHandle(), permission)
        } else {
            context.sendBroadcast(intent, permission)
        }
    }
}
