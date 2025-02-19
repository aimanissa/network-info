package com.aimanissa.base.navigation.domain

import com.aimanissa.base.navigation.screens.ConnectionScreen

@Suppress("TooManyFunctions")
interface ScreenProvider {

    val destinations: Destinations

    fun connectionScreen(): ConnectionScreen
}
