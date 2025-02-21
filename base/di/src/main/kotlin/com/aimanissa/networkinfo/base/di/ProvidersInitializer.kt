package com.aimanissa.networkinfo.base.di

import com.aimanissa.networkinfo.data.providers.ConnectionProviderImpl
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import org.koin.core.module.Module

object ProvidersInitializer : Initializer {

    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            factory<ConnectionProvider> { ConnectionProviderImpl() }
        }
    }
}