package com.aimanissa.networkinfo

import android.app.Application
import com.aimanissa.features.connection.di.connectionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@Suppress("unused")
class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initKoin()
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@BaseApp)
            androidLogger()
            modules(
                listOf(
                    appModule,
                    connectionModule,
                )
            )
        }
    }
}
