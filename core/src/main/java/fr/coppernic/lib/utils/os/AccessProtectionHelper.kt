package fr.coppernic.lib.utils.os

import android.content.Context
import android.os.Binder
import android.util.Pair
import fr.coppernic.lib.utils.log.LogDefines.LOG

@Suppress("MemberVisibilityCanBePrivate")
class AccessProtectionHelper constructor(val context: Context, val whiteList: HashSet<Pair<String, String>>) {

    /**
     * Checks if process that binds to this service (i.e. the package name corresponding to the
     * process) is in the whitelist.
     *
     * @return true if process is allowed to use this service
     */
    fun isCallerAllowed(): Boolean {
        return isUidAllowed(Binder.getCallingUid())
    }

    /**
     * Checks if process that correspond to this uid is allowed regarding provided white list
     *
     * @return true if process is allowed
     */
    fun isUidAllowed(uid: Int): Boolean {
        val callingPackages = context.packageManager.getPackagesForUid(uid)
                ?: throw RuntimeException("Should not happen. No packages associated to caller UID!")

        // is calling package allowed to use this service?
        // NOTE: No support for sharedUserIds
        // callingPackages contains more than one entry when sharedUserId has been used
        // No plans to support sharedUserIds due to many bugs connected to them:
        // http://java-hamster.blogspot.de/2010/05/androids-shareduserid.html
        val currentPkg = callingPackages[0]
        return isPackageAllowed(currentPkg)
    }


    /**
     * Checks if process that correspond to this package is allowed regarding provided white list
     *
     * @return true if process is allowed
     */
    fun isPackageAllowed(packageName: String): Boolean {
        val remoteSignature = AppHelper.getAppSignaturesSHA256(context, packageName)
        LOG.trace("Checking if package $packageName with signature $remoteSignature is allowed to access privileged extension")

        whiteList.forEach {
            if (packageName.equals(it.first, true) && remoteSignature.equals(it.second, true)) {
                return true
            }
        }
        return false
    }
}
