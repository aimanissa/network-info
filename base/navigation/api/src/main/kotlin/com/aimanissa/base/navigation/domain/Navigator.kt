package com.aimanissa.base.navigation.domain

import kotlinx.coroutines.flow.SharedFlow

const val KEY_NAVIGATOR = "key_navigator"

sealed interface Command {
    data class Route(val screen: String, val isRoot: Boolean) : Command
    data class Back(val screen: String?) : Command
    data class OpenUrl(val url: String) : Command
    data class OpenApp(val packageName: String, val googlePlayUrl: String?, val hmsUrl: String?) : Command
    data class Call(val phone: String) : Command
}

interface Navigator {
    val routeFlow: SharedFlow<Command>
    suspend fun navigateTo(target: String, isRoot: Boolean = false)
    suspend fun backTo(target: String? = null)
    suspend fun openUrl(url: String)
    suspend fun openApp(packageName: String, googlePlayUrl: String?, hmsUrl: String? = null)
    suspend fun call(phone: String)
}
