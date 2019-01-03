package fr.coppernic.lib.utils.os

import android.content.Context
import android.content.Intent
import android.net.Uri
import fr.coppernic.lib.utils.graphics.BitmapHelper
import timber.log.Timber

const val SETTINGS_QUICK_LAUNCH_TITLE = "com.android.settings.quicklaunch.TITLE"
const val SETTINGS_QUICK_LAUNCH_PACKAGENAME = "com.android.settings.quicklaunch.PACKAGENAME"
const val SETTINGS_QUICK_LAUNCH_SHORTCUT = "com.android.settings.quicklaunch.SHORTCUT"

object IntentHelper {

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     */
    fun createGetContentIntent(): Intent {

        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.type = "*/*"
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
        return intent
    }

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     */
    fun createGetLocalContentIntent(): Intent {

        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.type = "*/*"
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        return intent
    }

    /**
     * Get quick launch intent from application package
     *
     * This intent can be used to map an application to a lateral button.
     *
     * @param context     Android context
     * @param packageName Application's package
     * @return Quick launch intent or null if no application found
     */
    fun getQuickLaunchIntent(context: Context, packageName: String): Intent? {
        var ret: Intent? = null
        val pm = context.packageManager
        val i = pm.getLaunchIntentForPackage(packageName)
        if (i == null) {
            Timber.e("Package not found : $packageName")
        } else {
            val listActivities = pm.queryIntentActivities(i, 0)

            Timber.d("the res size is: %s", listActivities.size)

            if (listActivities.size > 0) {
                val info = listActivities[0]
                val ai = info.activityInfo

                ret = i
                ret.setClassName(ai.packageName, ai.name)
                ret.putExtra(SETTINGS_QUICK_LAUNCH_TITLE, info.loadLabel(pm))
                ret.putExtra(SETTINGS_QUICK_LAUNCH_PACKAGENAME, ai.packageName)
                ret.putExtra(SETTINGS_QUICK_LAUNCH_SHORTCUT, "")
            } else {
                Timber.e("Package not found : $packageName")
            }
        }
        return ret
    }


    /**
     * Get shortcut intent from application package
     *
     * This intent can be used for installing application shortcut on screen
     *
     * @param context     Android context
     * @param packageName Application's package
     * @return Shortcut intent or null if no application found
     */
    fun getShortCutIntent(context: Context, packageName: String): Intent? {
        var ret: Intent? = null
        val pm = context.packageManager
        val i = pm.getLaunchIntentForPackage(packageName)
        if (i == null) {
            Timber.e("Package not found : $packageName")
        } else {
            val listActivities = pm.queryIntentActivities(i, 0)

            Timber.d("the res size is: %s", listActivities.size)

            if (listActivities.size > 0) {
                val info = listActivities[0]
                val ai = info.activityInfo

                ret = Intent()
                ret.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i)
                ret.putExtra(Intent.EXTRA_SHORTCUT_NAME, ai.loadLabel(pm))
                ret.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapHelper.drawableToBitmap(ai.loadIcon(pm)))
                ret.putExtra("duplicate", false)
            } else {
                Timber.e("Package not found : $packageName")
            }
        }
        return ret
    }

    /**
     * Get an intent to show app details
     *
     * @param packageName app's package name
     * @return intent
     */
    fun getShowApplicationInfoIntent(packageName: String): Intent {
        val i = Intent()
        i.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:$packageName")
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return i
    }

    fun getShowGpsSettingIntent(): Intent {
        return Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    }

    /**
     * Get intent to uninstall package via android system
     *
     * @param packName Application id
     * @return Intent to send
     */
    fun getUninstallIntent(packName: String): Intent {
        return Intent(Intent.ACTION_UNINSTALL_PACKAGE,
                Uri.parse("package:$packName"))
    }

    /**
     * Get intent to shutdown android system
     *
     * @return Intent to send
     */
    fun getShutdownIntent(): Intent {
        val i = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN")
        i.putExtra("android.intent.extra.KEY_CONFIRM", false)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return i
    }

    /**
     * Return TRUE is intent is an intent launcher
     *
     * @param intent intent
     * @return true is intent is an intent launcher, false otherwise
     */
    fun isRunningFromHome(intent: Intent): Boolean {
        return (intent.action == null || intent.action == Intent.ACTION_MAIN && intent.categories.contains(Intent.CATEGORY_LAUNCHER))
    }

}