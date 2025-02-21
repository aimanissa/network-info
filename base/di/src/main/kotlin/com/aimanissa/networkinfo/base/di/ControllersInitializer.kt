package com.aimanissa.networkinfo.base.di

import com.aimanissa.networkinfo.data.CellController
import com.aimanissa.networkinfo.data.WifiController
import org.koin.core.module.Module

object ControllersInitializer : Initializer {

    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            single { WifiController(context = get(), provider = get()) }
            single { CellController(context = get(), provider = get()) }
        }
    }
}