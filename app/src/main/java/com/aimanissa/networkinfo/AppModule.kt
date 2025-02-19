package com.aimanissa.networkinfo

import com.aimanissa.base.navigation.di.NavigationInitializer
import org.koin.dsl.module

val modules = listOf(
    NavigationInitializer,
)

val appModule = module {
    modules.forEach {
        it.initialize(this, MainActivity::class.java)
    }
}
