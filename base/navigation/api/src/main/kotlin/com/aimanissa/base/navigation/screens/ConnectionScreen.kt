package com.aimanissa.base.navigation.screens

import com.aimanissa.base.navigation.domain.ComposableFeatureEntry

abstract class ConnectionScreen : ComposableFeatureEntry {

    final override val featureRoute = SCREEN_NAME

    fun destination() = featureRoute

    protected companion object {
        const val SCREEN_NAME = "connection"
    }
}
