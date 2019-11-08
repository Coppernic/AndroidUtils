package fr.coppernic.lib.utils.res

import android.content.Context


fun Int.toResString(context: Context): String {
    return context.getString(this)
}

fun Int.toResString(context: Context, vararg arguments: Any): String {
    return context.getString(this, *arguments)
}
