package fr.coppernic.lib.utils.ui

import android.os.Handler
import android.os.Looper
import fr.coppernic.lib.utils.os.AppHelper

class UiHandler : Handler(Looper.getMainLooper()) {
    fun runOnUiThread(r: Runnable) {
        if (AppHelper.isUiThread()) {
            r.run()
        } else {
            this.post(r)
        }
    }
}
