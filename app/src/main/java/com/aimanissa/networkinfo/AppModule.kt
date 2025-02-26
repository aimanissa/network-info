package com.aimanissa.networkinfo

import com.aimanissa.networkinfo.base.di.ControllersInitializer
import com.aimanissa.networkinfo.base.di.NavigationInitializer
import com.aimanissa.networkinfo.base.di.InteractorsInitializer
import com.aimanissa.networkinfo.base.di.ProvidersInitializer
import com.aimanissa.networkinfo.base.di.RepositoriesInitializer
import org.koin.dsl.module

val modules = listOf(
    NavigationInitializer,
    ControllersInitializer,
    ProvidersInitializer,
    RepositoriesInitializer,
    InteractorsInitializer,
)

val appModule = module {
    modules.forEach {
        it.initialize(this, MainActivity::class.java)
    }
}
