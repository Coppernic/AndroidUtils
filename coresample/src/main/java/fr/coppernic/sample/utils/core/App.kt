package fr.coppernic.sample.utils.core

import android.app.Application
import fr.coppernic.sample.utils.core.di.components.AppComponents
import fr.coppernic.sample.utils.core.di.components.DaggerAppComponents
import fr.coppernic.sample.utils.core.di.modules.ContextModule
import timber.log.Timber

class App : Application() {

    companion object {
        @JvmStatic
        lateinit var appComponents: AppComponents
    }

    override fun onCreate() {
        super.onCreate()
        setupDi()
        setupLog()
    }

    private fun setupDi() {
        appComponents = DaggerAppComponents.builder()
                .contextModule(ContextModule(this))
                .build()
    }

    private fun setupLog() {
        Timber.plant(Timber.DebugTree())
    }
}
