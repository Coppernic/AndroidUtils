package fr.coppernic.lib.utils.io

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build


fun Int.toResUri(context: Context): Uri {
    return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.resources.getResourcePackageName(this) + '/' +
            context.resources.getResourceTypeName(this) + '/' +
            context.resources.getResourceEntryName(this))
}

@Suppress("DEPRECATION")
fun Context.getMyColor(resId: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        resources.getColor(resId, theme)
    } else {
        resources.getColor(resId)
    }
}
