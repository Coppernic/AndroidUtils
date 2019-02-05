package fr.coppernic.lib.utils.text

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


object SoftKeyboardHelper {

    fun closeKeyboard(activity: Activity) {
        val input = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            input.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun openKeyboard(activity: Activity) {
        val input = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            input.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_IMPLICIT, 0)
        }
    }

    fun hideKeyboard(activity: Activity) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }
}
