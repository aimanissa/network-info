package com.aimanissa.networkinfo.base.di

import com.aimanissa.networkinfo.data.repos.ConnectionRepositoryImpl
import com.aimanissa.networkinfo.domain.repos.ConnectionRepository
import org.koin.core.module.Module

object RepositoriesInitializer : Initializer {
    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            single<ConnectionRepository> { ConnectionRepositoryImpl(provider = get()) }

        }
    }
}
