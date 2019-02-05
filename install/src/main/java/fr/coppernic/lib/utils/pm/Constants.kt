package fr.coppernic.lib.utils.pm

import android.content.pm.IPackageDeleteObserver
import android.content.pm.IPackageInstallObserver

/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] on
 * success.
 */
const val INSTALL_SUCCEEDED = 1
const val DELETE_SUCCEEDED = 1
const val RETCODE_OK = 0
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the package is
 * already installed.
 */
const val INSTALL_FAILED_ALREADY_EXISTS = -1
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the package archive
 * file is invalid.
 */
const val INSTALL_FAILED_INVALID_APK = -2
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the URI passed in
 * is invalid.
 */
const val INSTALL_FAILED_INVALID_URI = -3
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the package manager
 * service found that the device didn't have enough storage space to install
 * the app.
 */
const val INSTALL_FAILED_INSUFFICIENT_STORAGE = -4
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * a
 * package is already installed with the same name.
 */
const val INSTALL_FAILED_DUPLICATE_PACKAGE = -5
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the requested shared user does not exist.
 */
const val INSTALL_FAILED_NO_SHARED_USER = -6
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * a previously installed package of the same name has a different signature
 * than the new package (and the old package's data was not removed).
 */
const val INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package is requested a shared user which is already installed on
 * the
 * device and does not have matching signature.
 */
const val INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package uses a shared library that is not available.
 */
const val INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package uses a shared library that is not available.
 */
const val INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package failed while optimizing and validating its dex files,
 * either because there was not enough storage or the validation failed.
 */
const val INSTALL_FAILED_DEXOPT = -11
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package failed because the current SDK version is older than
 * that required by the package.
 */
const val INSTALL_FAILED_OLDER_SDK = -12
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package failed because it contains a content provider with the
 * same authority as a provider already installed in the system.
 */
const val INSTALL_FAILED_CONFLICTING_PROVIDER = -13
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package failed because the current SDK version is newer than
 * that required by the package.
 */
const val INSTALL_FAILED_NEWER_SDK = -14
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package failed because it has specified that it is a test-only
 * package and the caller has not supplied the INSTALL_ALLOW_TEST
 * flag.
 */
const val INSTALL_FAILED_TEST_ONLY = -15
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the package being installed contains native code, but none that is
 * compatible with the the device's CPU_ABI.
 */
const val INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package uses a feature that is not available.
 */
const val INSTALL_FAILED_MISSING_FEATURE = -17
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * a secure container mount point couldn't be accessed on external media.
 */
const val INSTALL_FAILED_CONTAINER_ERROR = -18
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package couldn't be installed in the specified install
 * location.
 */
const val INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19
/**
 * Installation return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the new package couldn't be installed in the specified install
 * location because the media is not available.
 */
const val INSTALL_FAILED_MEDIA_UNAVAILABLE = -20
/**
 * Installation return code: this is passed to the [IPackageInstallObserver] by
 *
 * @link #installPackage(android.net.Uri)} if
 * the new package couldn't be installed because the verification timed out.
 */
const val INSTALL_FAILED_VERIFICATION_TIMEOUT = -21
/**
 * Installation return code: this is passed to the [IPackageInstallObserver] by
 *
 * @link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if
 * the new package couldn't be installed because the verification did not succeed.
 */
const val INSTALL_FAILED_VERIFICATION_FAILURE = -22
/**
 * Installation return code: this is passed to the [IPackageInstallObserver] by
 *
 * @link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if
 * the package changed from what the calling program expected.
 */
const val INSTALL_FAILED_PACKAGE_CHANGED = -23
/**
 * Installation return code: this is passed to the [IPackageInstallObserver] by
 *
 * @link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if
 * the new package is assigned a different UID than it previously held.
 */
const val INSTALL_FAILED_UID_CHANGED = -24
/**
 * Installation return code: this is passed to the [IPackageInstallObserver] by
 *
 * @link #installPackage(android.net.Uri, IPackageInstallObserver, int)} if
 * the new package has an older version code than the currently installed package.
 */
const val INSTALL_FAILED_VERSION_DOWNGRADE = -25

// ------ Errors related to sdcard
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser was given a path that is not a file, or does not end with the
 * expected
 * '.apk' extension.
 */
const val INSTALL_PARSE_FAILED_NOT_APK = -100
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser was unable to retrieve the AndroidManifest.xml file.
 */
const val INSTALL_PARSE_FAILED_BAD_MANIFEST = -101
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser encountered an unexpected exception.
 */
const val INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser did not find any certificates in the .apk.
 */
const val INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser found inconsistent certificates on the files in the .apk.
 */
const val INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser encountered a CertificateEncodingException in one of the
 * files in the .apk.
 */
const val INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser encountered a bad or missing package name in the manifest.
 */
const val INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser encountered a bad shared user id name in the manifest.
 */
const val INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser encountered some structural problem in the manifest.
 */
const val INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108
/**
 * Installation parse return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the parser did not find any actionable tags (instrumentation or
 * application)
 * in the manifest.
 */
const val INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109
/**
 * Installation failed return code: this is passed to the
 * [IPackageInstallObserver] by
 * [.installPackage] if
 * the system failed to install the package because of system issues.
 */
const val INSTALL_FAILED_INTERNAL_ERROR = -110
const val INSTALL_REPLACE_EXISTING = 2

/**
 * Flag parameter for [.deletePackage] to indicate that you don't want to delete the
 * package's data directory.
 */
const val DELETE_KEEP_DATA = 0x00000001
/**
 * Flag parameter for [.deletePackage] to indicate that you want the
 * package deleted for all users.
 */
const val DELETE_ALL_USERS = 0x00000002
/**
 * Flag parameter for [.deletePackage] to indicate that, if you are calling
 * uninstall on a system that has been updated, then don't do the normal process
 * of uninstalling the update and rolling back to the older system version (which
 * needs to happen for all users); instead, just mark the app as uninstalled for
 * the current user.
 */
const val DELETE_SYSTEM_APP = 0x00000004
/**
 * Flag parameter for [.deletePackage] to indicate that, if you are calling
 * uninstall on a package that is replaced to provide new feature splits, the
 * existing application should not be killed during the removal process.
 */
const val DELETE_DONT_KILL_APP = 0x00000008

/**
 * Deletion failed return code: this is passed to the
 * [IPackageDeleteObserver] if the system failed to delete the package
 * for an unspecified reason.
 */
const val DELETE_FAILED_INTERNAL_ERROR = -1
/**
 * Deletion failed return code: this is passed to the
 * [IPackageDeleteObserver] if the system failed to delete the package
 * because it is the active DevicePolicy manager.
 */
const val DELETE_FAILED_DEVICE_POLICY_MANAGER = -2
/**
 * Deletion failed return code: this is passed to the
 * [IPackageDeleteObserver] if the system failed to delete the package
 * since the user is restricted.
 */
const val DELETE_FAILED_USER_RESTRICTED = -3
/**
 * Deletion failed return code: this is passed to the
 * [IPackageDeleteObserver] if the system failed to delete the package
 * because a profile or device owner has marked the package as
 * uninstallable.
 */
const val DELETE_FAILED_OWNER_BLOCKED = -4
const val DELETE_FAILED_ABORTED = -5
/**
 * Deletion failed return code: this is passed to the
 * [IPackageDeleteObserver] if the system failed to delete the package
 * because the packge is a shared library used by other installed packages.
 * */
const val DELETE_FAILED_USED_SHARED_LIBRARY = -6

internal const val BROADCAST_ACTION_INSTALL = "fr.coppernic.lib.utils.install.ACTION_INSTALL_COMMIT"
internal const val BROADCAST_ACTION_UNINSTALL = "fr.coppernic.lib.utils.install.ACTION_UNINSTALL_COMMIT"
internal const val BROADCAST_SENDER_PERMISSION = "android.permission.INSTALL_PACKAGES"
internal const val EXTRA_LEGACY_STATUS = "android.content.pm.extra.LEGACY_STATUS"

object Constants {
    fun codeToString(code: Int): String {
        when (code) {
            INSTALL_SUCCEEDED -> return "SUCCEEDED"
            INSTALL_FAILED_ALREADY_EXISTS -> return "INSTALL_FAILED_ALREADY_EXISTS"
            INSTALL_FAILED_INVALID_APK -> return "INSTALL_FAILED_INVALID_APK"
            INSTALL_FAILED_INVALID_URI -> return "INSTALL_FAILED_INVALID_URI"
            INSTALL_FAILED_INSUFFICIENT_STORAGE -> return "INSTALL_FAILED_INSUFFICIENT_STORAGE"
            INSTALL_FAILED_DUPLICATE_PACKAGE -> return "INSTALL_FAILED_DUPLICATE_PACKAGE"
            INSTALL_FAILED_NO_SHARED_USER -> return "INSTALL_FAILED_NO_SHARED_USER"
            INSTALL_FAILED_UPDATE_INCOMPATIBLE -> return "INSTALL_FAILED_UPDATE_INCOMPATIBLE"
            INSTALL_FAILED_SHARED_USER_INCOMPATIBLE -> return "INSTALL_FAILED_SHARED_USER_INCOMPATIBLE"
            INSTALL_FAILED_MISSING_SHARED_LIBRARY -> return "INSTALL_FAILED_MISSING_SHARED_LIBRARY"
            INSTALL_FAILED_REPLACE_COULDNT_DELETE -> return "INSTALL_FAILED_REPLACE_COULDNT_DELETE"
            INSTALL_FAILED_DEXOPT -> return "INSTALL_FAILED_DEXOPT"
            INSTALL_FAILED_OLDER_SDK -> return "INSTALL_FAILED_OLDER_SDK"
            INSTALL_FAILED_CONFLICTING_PROVIDER -> return "INSTALL_FAILED_CONFLICTING_PROVIDER"
            INSTALL_FAILED_NEWER_SDK -> return "INSTALL_FAILED_NEWER_SDK"
            INSTALL_FAILED_TEST_ONLY -> return "INSTALL_FAILED_TEST_ONLY"
            INSTALL_FAILED_CPU_ABI_INCOMPATIBLE -> return "INSTALL_FAILED_CPU_ABI_INCOMPATIBLE"
            INSTALL_FAILED_MISSING_FEATURE -> return "INSTALL_FAILED_MISSING_FEATURE"
            INSTALL_FAILED_CONTAINER_ERROR -> return "INSTALL_FAILED_CONTAINER_ERROR"
            INSTALL_FAILED_INVALID_INSTALL_LOCATION -> return "INSTALL_FAILED_INVALID_INSTALL_LOCATION"
            INSTALL_FAILED_MEDIA_UNAVAILABLE -> return "INSTALL_FAILED_MEDIA_UNAVAILABLE"
            INSTALL_FAILED_VERIFICATION_TIMEOUT -> return "INSTALL_FAILED_VERIFICATION_TIMEOUT"
            INSTALL_FAILED_VERIFICATION_FAILURE -> return "INSTALL_FAILED_VERIFICATION_FAILURE"
            INSTALL_FAILED_PACKAGE_CHANGED -> return "INSTALL_FAILED_PACKAGE_CHANGED"
            INSTALL_FAILED_UID_CHANGED -> return "INSTALL_FAILED_UID_CHANGED"
            INSTALL_FAILED_VERSION_DOWNGRADE -> return "INSTALL_FAILED_VERSION_DOWNGRADE"
            INSTALL_PARSE_FAILED_NOT_APK -> return "INSTALL_PARSE_FAILED_NOT_APK"
            INSTALL_PARSE_FAILED_BAD_MANIFEST -> return "INSTALL_PARSE_FAILED_BAD_MANIFEST"
            INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION -> return "INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION"
            INSTALL_PARSE_FAILED_NO_CERTIFICATES -> return "INSTALL_PARSE_FAILED_NO_CERTIFICATES"
            INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES -> return "INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES"
            INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING -> return "INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING"
            INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME -> return "INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME"
            INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID -> return "INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID"
            INSTALL_PARSE_FAILED_MANIFEST_MALFORMED -> return "INSTALL_PARSE_FAILED_MANIFEST_MALFORMED"
            INSTALL_PARSE_FAILED_MANIFEST_EMPTY -> return "INSTALL_PARSE_FAILED_MANIFEST_EMPTY"
            INSTALL_FAILED_INTERNAL_ERROR -> return "INSTALL_FAILED_INTERNAL_ERROR"
        }
        return "UNKNOWN ERROR : $code"
    }
}
