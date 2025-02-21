package com.aimanissa.networkinfo.base.di

import org.koin.core.module.Module

interface Initializer {

    fun initialize(appModule: Module, activity: Class<*>)
}
