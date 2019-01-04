package fr.coppernic.lib.utils.pm

interface PackageObserver {
    fun onPackageDeleted(packageName: String, returnCode: Int)

    fun onPackageInstalled(packageName: String, returnCode: Int)
}
