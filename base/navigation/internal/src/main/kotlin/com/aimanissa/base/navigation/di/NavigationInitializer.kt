package com.aimanissa.base.navigation.di

import com.aimanissa.base.core.di.Initializer
import com.aimanissa.base.navigation.domain.Navigator
import com.aimanissa.base.navigation.domain.ScreenProvider
import com.aimanissa.base.navigation.domain.NavigatorImpl
import com.aimanissa.base.navigation.domain.ScreenProviderImpl
import org.koin.core.module.Module

object NavigationInitializer : Initializer {
    override fun initialize(appModule: Module, activity: Class<*>) {
        appModule.run {
            single<ScreenProvider> { ScreenProviderImpl() }
            single<Navigator> { NavigatorImpl() }
        }
    }
}
