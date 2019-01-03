package fr.coppernic.lib.utils.graphics

import android.content.Context
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import timber.log.Timber


object ColorHelper {

    /**
     * @param color can be ColorInt or ColorRes
     */
    @ColorInt
    operator fun get(context: Context, color: Int): Int {
        return try {
            ContextCompat.getColor(context, color)
        } catch (e: Exception) {
            color
        }
    }

    @ColorInt
    fun getAttributeColor(context: Context?, @AttrRes attr: Int): Int {
        if (context == null) {
            Timber.e("getAttributeColor() context is null")
            return Color.WHITE
        }

        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    fun isValidColor(string: String): Boolean {
        return try {
            Color.parseColor(string)
            true
        } catch (e: Exception) {
            false
        }

    }
}