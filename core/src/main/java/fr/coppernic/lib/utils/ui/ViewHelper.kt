package fr.coppernic.lib.utils.ui

import android.content.res.Resources
import android.view.View

fun View.getHumanReadableId(): String {
    val out = StringBuilder()
    if (id != View.NO_ID) {
        val r = context.resources
        if (id != 0 && id.ushr(24) != 0 && r != null) {
            try {
                val pkgname: String
                when (id and -0x1000000) {
                    0x7f000000 -> pkgname = "app"
                    0x01000000 -> pkgname = "android"
                    else -> pkgname = r.getResourcePackageName(id)
                }
                val typename = r.getResourceTypeName(id)
                val entryname = r.getResourceEntryName(id)
                out.append(" ")
                out.append(pkgname)
                out.append(":")
                out.append(typename)
                out.append("/")
                out.append(entryname)
            } catch (e: Resources.NotFoundException) {
            }
        }
    }
    return out.toString()
}
