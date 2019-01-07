package fr.coppernic.lib.utils.pm

import android.Manifest
import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.IPackageDeleteObserver
import android.content.pm.IPackageInstallObserver
import android.content.pm.PackageInstaller
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import fr.coppernic.lib.utils.io.Closeables
import fr.coppernic.lib.utils.io.Disposable
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicBoolean

private const val EXTRA_PACKAGE_NAME = "android.content.pm.extra.PACKAGE_NAME"
private const val STATUS_FAILURE = 1

class PackageManagerHelper constructor(private val context: Context) : Disposable {

    private val installMethod: Method
    private val uninstallMethod: Method
    private val registered = AtomicBoolean(false)
    private var state = State.IDLE

    private val installObserver = object : IPackageInstallObserver.Stub() {
        override fun packageInstalled(packageName: String, returnCode: Int) {
            packageObserver.onPackageInstalled(packageName, returnCode)
        }
    }

    private val deleteObserver = object : IPackageDeleteObserver.Stub() {
        override fun packageDeleted(packageName: String, returnCode: Int) {
            packageObserver.onPackageDeleted(packageName, returnCode)
        }
    }

    var packageObserver = object : PackageObserver {
        override fun onPackageDeleted(packageName: String, returnCode: Int) {
        }

        override fun onPackageInstalled(packageName: String, returnCode: Int) {
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val returnCode = intent.getIntExtra(EXTRA_LEGACY_STATUS, STATUS_FAILURE)
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
            when (state) {
                State.INSTALLING -> packageObserver.onPackageInstalled(packageName, returnCode)
                State.UNINSTALLING -> packageObserver.onPackageDeleted(packageName, returnCode)
                State.IDLE -> {
                }
            }
            state = State.IDLE
        }
    }

    init {
        val types = arrayOf<Class<*>>(Uri::class.java,
                IPackageInstallObserver::class.java,
                Int::class.java,
                String::class.java)
        val uninstallTypes = arrayOf<Class<*>>(String::class.java,
                IPackageDeleteObserver::class.java,
                Int::class.java)

        val pm = context.packageManager

        installMethod = pm.javaClass.getMethod("installPackage", *types)
        uninstallMethod = pm.javaClass.getMethod("deletePackage", *uninstallTypes)
    }

    override fun isDisposed(): Boolean {
        return !registered.get()
    }

    override fun dispose() {
        unregister()
    }

    /**
     * Uninstall package
     *
     * @param packageName Name of the package to uninstall
     * @throws PackageException
     */
    @Throws(PackageException::class)
    fun uninstallPackage(packageName: String, flags: Int = 0) {
        if (Build.VERSION.SDK_INT >= 24) {
            uninstallPackageStage(packageName)
        } else {
            if (isDeviceOwner(packageName)) {
                Timber.e("Cannot delete $packageName. This app is the device owner.")
                return
            }
            try {
                uninstallMethod.invoke(context.packageManager, packageName, deleteObserver, flags)
            } catch (e: Exception) {
                throw PackageException(e)
            }
        }
    }

    /**
     * Install an application
     *
     * @param apkFile Path to the apk file
     * @throws IllegalArgumentException
     * @throws PackageException
     */
    @Throws(IllegalArgumentException::class, PackageException::class)
    fun installPackage(apkFile: String) {
        installPackage(File(apkFile))
    }

    /**
     * Install an application
     *
     * @param apkFile File to the apk to install
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Throws(IllegalArgumentException::class, PackageException::class)
    fun installPackage(apkFile: File) {
        if (!apkFile.exists()) {
            throw IllegalArgumentException()
        }
        //FIXME use fileProvider on resent Android OS
        val packageURI = Uri.fromFile(apkFile)
        installPackage(packageURI)
    }

    /**
     * Install an application
     *
     * @param packageUri Uri to the apk to install
     * @throws PackageException
     */
    @Throws(PackageException::class)
    fun installPackage(packageUri: Uri, flags: Int = INSTALL_REPLACE_EXISTING, installerPackageName: String? = null) {
        if (Build.VERSION.SDK_INT >= 24) {
            doPackageStage(packageUri)
        } else {
            try {
                installMethod.invoke(context.packageManager, packageUri, installObserver, flags, installerPackageName)
            } catch (e: Exception) {
                throw PackageException(e)
            }
        }
    }

    private fun register() {
        if (!registered.get()) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BROADCAST_ACTION_INSTALL)
            intentFilter.addAction(BROADCAST_ACTION_UNINSTALL)
            context.applicationContext.registerReceiver(broadcastReceiver, intentFilter)
            registered.set(true)
        }
    }

    private fun unregister() {
        try {
            context.applicationContext.unregisterReceiver(broadcastReceiver)
        } catch (ignore: Exception) {
        } finally {
            registered.set(false)
        }
    }

    /**
     * Below function is copied mostly as-is from
     * https://android.googlesource.com/platform/packages/apps/PackageInstaller/+/06163dec5a23bb3f17f7e6279f6d46e1851b7d16
     */
    @TargetApi(24)
    private fun doPackageStage(packageURI: Uri) {
        try {
            val inputStream = context.contentResolver.openInputStream(packageURI)
            if (inputStream != null) {
                doPackageStage(inputStream)
            }
        } catch (e: IOException) {
            Timber.d(e, "Failure")
        }
    }

    @TargetApi(24)
    private fun doPackageStage(inputStream: InputStream) {
        register()

        val pm = context.packageManager
        val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val packageInstaller = pm.packageInstaller
        var session: PackageInstaller.Session? = null
        try {
            val sessionId = packageInstaller.createSession(params)
            val buffer = ByteArray(65536)
            session = packageInstaller.openSession(sessionId)
            val out = session.openWrite("PackageInstaller", 0, -1 /* sizeBytes, unknown */)
            try {
                var c = inputStream.read(buffer)
                while (c != -1) {
                    out.write(buffer, 0, c)
                    c = inputStream.read(buffer)
                }
                session.fsync(out)
            } finally {
                Closeables.closeQuietly(inputStream)
                Closeables.closeQuietly(out)
            }
            // Create a PendingIntent and use it to generate the IntentSender
            val broadcastIntent = Intent(BROADCAST_ACTION_INSTALL)
            val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    sessionId,
                    broadcastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            session.commit(pendingIntent.intentSender)
        } catch (e: IOException) {
            Timber.d(e, "Failure")
        } finally {
            Closeables.closeQuietly(session)
        }
    }


    @TargetApi(24)
    private fun uninstallPackageStage(packageName: String) {
        register()

        val pm = context.packageManager
        val packageInstaller = pm.packageInstaller

        /*
         * The client app used to set this to F-Droid, but we need it to be set to
         * this package's package name to be able to uninstall from here.
         */
        pm.setInstallerPackageName(packageName, context.packageName)
        // Create a PendingIntent and use it to generate the IntentSender
        val broadcastIntent = Intent(BROADCAST_ACTION_UNINSTALL)
        val pendingIntent = PendingIntent.getBroadcast(
                context, // context
                0, // arbitary
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        packageInstaller.uninstall(packageName, pendingIntent.intentSender)
    }

    private fun hasPrivilegedPermissionsImpl(): Boolean {
        val hasInstallPermission = context.packageManager.checkPermission(Manifest.permission.INSTALL_PACKAGES,
                context.packageName) == PackageManager.PERMISSION_GRANTED
        val hasDeletePermission = context.packageManager.checkPermission(Manifest.permission.DELETE_PACKAGES,
                context.packageName) == PackageManager.PERMISSION_GRANTED

        return hasInstallPermission && hasDeletePermission
    }

    /**
     * Checks if an app is the current device owner.
     *
     * @param packageName to check
     * @return true if it is the device owner app
     */
    private fun isDeviceOwner(packageName: String): Boolean {
        if (Build.VERSION.SDK_INT < 18) {
            return false
        }

        val manager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager?
        return manager?.isDeviceOwnerApp(packageName) ?: false
    }
}

private enum class State {
    INSTALLING,
    UNINSTALLING,
    IDLE
}
