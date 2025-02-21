package com.aimanissa.features.connection.di

import com.aimanissa.features.connection.ui.ConnectionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val connectionModule = module {
    viewModel {
        ConnectionViewModel(
            screenProvider = get(),
            navigator = get(),
            connectionInteractor = get(),
        )
    }
}
