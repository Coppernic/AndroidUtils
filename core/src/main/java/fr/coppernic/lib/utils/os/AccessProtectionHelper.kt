package fr.coppernic.lib.utils.os

import android.content.Context
import android.os.Binder
import fr.coppernic.lib.utils.log.LogDefines
import fr.coppernic.lib.utils.log.LogDefines.LOG
import timber.log.Timber
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
/**
 * Help to know if an application is allowed to connect to this service
 *
 * White list is built as followed :
 *
 * Map < Caller certificate's SHA-256 -> Set of packages that are corresponding to this certificate hash >
 *
 * @param context Android Context
 * @param whiteList List of applications allowed to connect to service
 */
class AccessProtectionHelper constructor(val context: Context, val whiteList: Map<String, Set<Regex>>) {

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
        return arePackagesAllowed(getPackageForUid(uid))
    }

    fun getPackageForUid(uid: Int): Set<String> {
        val callingPackages = context.packageManager.getPackagesForUid(uid)
                ?: throw RuntimeException("Should not happen. No packages associated to caller UID!")

        // is calling package allowed to use this service?
        // NOTE: No support for sharedUserIds
        // callingPackages contains more than one entry when sharedUserId has been used
        // No plans to support sharedUserIds due to many bugs connected to them:
        // http://java-hamster.blogspot.de/2010/05/androids-shareduserid.html
        return callingPackages.toSet()
    }

    /**
     * Checks if process that correspond to this package is allowed regarding provided white list
     *
     * @return true if process is allowed
     */
    fun arePackagesAllowed(packageNames: Set<String>): Boolean {
        // 1. Get a list of signature for all package names
        return packageNames.map {
            AppHelper.getAppSignaturesSHA256(context, it).toLowerCase(Locale.US)
        }.toSet().any() { remoteSignature ->
            // 2. For each signature, check access validity
            isPackageAllowed(remoteSignature, packageNames)
        }
    }

    fun isPackageAllowed(remoteSignature: String, packageNames: Set<String>): Boolean {
        // 3. Checking access validity for each packageNames

        if (LogDefines.verbose) {
            LOG.trace("Checking access for signature $remoteSignature and packages $packageNames")
        }

        val packagePatterns = whiteList[remoteSignature]
        return if (packagePatterns == null) {
            if (LogDefines.verbose) {
                Timber.v("No signature found in whitelist")
            }
            false
        } else {
            packageNames.any { name ->
                packagePatterns.any { pattern ->
                    name.matches(pattern).also {
                        if (LogDefines.verbose) {
                            LOG.trace("does $name matches ${pattern.pattern} : $it")
                        }
                    }
                }
            }
        }
    }
}
