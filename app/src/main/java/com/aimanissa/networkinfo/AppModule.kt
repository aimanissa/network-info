package com.aimanissa.networkinfo

import com.aimanissa.networkinfo.base.di.ControllersInitializer
import com.aimanissa.networkinfo.base.di.NavigationInitializer
import com.aimanissa.networkinfo.base.di.InteractorsInitializer
import com.aimanissa.networkinfo.base.di.ProvidersInitializer
import org.koin.dsl.module

val modules = listOf(
    NavigationInitializer,
    ProvidersInitializer,
    InteractorsInitializer,
    ControllersInitializer,
)

val appModule = module {
    modules.forEach {
        it.initialize(this, MainActivity::class.java)
    }
}
