package fr.coppernic.lib.utils.text

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager


object SoftKeyboardHelper {

    fun closeKeyboard(context: Context) {
        val input = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = (context as Activity).currentFocus
        if (view != null) {
            input.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openKeyboard(context: Context) {
        val input = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = (context as Activity).currentFocus
        if (view != null) {
            input.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_IMPLICIT, 0)
        }
    }
}
