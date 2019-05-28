package fr.coppernic.sample.utils.core.di.components

import dagger.Component
import fr.coppernic.sample.utils.core.di.modules.ContextModule
import fr.coppernic.sample.utils.core.home.HomeActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [(ContextModule::class)])
interface AppComponents {

    fun inject(homeActivity: HomeActivity)
}
