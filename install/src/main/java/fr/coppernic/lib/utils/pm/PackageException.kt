package fr.coppernic.lib.utils.pm

class PackageException : Exception {
    constructor() : super()
    constructor(cause: Throwable) : super(cause)
}
