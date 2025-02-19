package com.aimanissa.base.navigation.domain

import androidx.compose.runtime.compositionLocalOf
import com.aimanissa.base.navigation.domain.screens.ConnectionScreenImpl
import com.aimanissa.base.navigation.screens.ConnectionScreen


@Suppress("TooManyFunctions")
class ScreenProviderImpl : ScreenProvider {
    override val destinations: Destinations = mapOf(
        ConnectionScreen::class.java to ConnectionScreenImpl(),
    )

    override fun connectionScreen(): ConnectionScreen = destinations.find()
}

val LocalAppProvider = compositionLocalOf<ScreenProvider> { error("No app provider found!") }
