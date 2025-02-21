package com.aimanissa.networkinfo.base.di

import com.aimanissa.networkinfo.domain.interactors.ConnectionInteractor
import com.aimanissa.networkinfo.domain.interactors.ConnectionInteractorImpl
import org.koin.core.module.Module

object InteractorsInitializer : Initializer {

    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            factory<ConnectionInteractor> {
                ConnectionInteractorImpl(repository = get())
            }
        }
    }
}