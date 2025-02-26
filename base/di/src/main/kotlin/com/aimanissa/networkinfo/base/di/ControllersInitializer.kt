package com.aimanissa.networkinfo.base.di

import com.aimanissa.base.core.workers.LifecycleSubscriptionWorker
import com.aimanissa.base.core.workers.SyncLifecycleService
import com.aimanissa.base.core.workers.SyncService
import com.aimanissa.networkinfo.data.controllers.cell.CellController
import com.aimanissa.networkinfo.data.controllers.wifi.WifiController
import org.koin.core.module.Module
import org.koin.dsl.binds

object ControllersInitializer : Initializer {

    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            single {
                SyncLifecycleService(
                    workers = listOf(
                        get<LifecycleSubscriptionWorker>(),
                    )
                )
            } binds arrayOf(SyncService::class)

            single { WifiController(context = get(), provider = get()) }
            single { CellController(context = get(), provider = get()) }
        }
    }
}