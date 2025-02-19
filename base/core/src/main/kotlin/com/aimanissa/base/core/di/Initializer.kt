package com.aimanissa.base.core.di

import org.koin.core.module.Module

interface Initializer {

    fun initialize(appModule: Module, activity: Class<*>)
}
