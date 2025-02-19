package com.aimanissa.base.navigation.domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NavigatorImpl : Navigator {

    private val _routeFlow = MutableSharedFlow<Command>(
        extraBufferCapacity = EXTRA_BUFFER_CAPACITY_VALUE,
        replay = REPLAY_VALUE
    )

    override val routeFlow = _routeFlow.asSharedFlow()

    override suspend fun navigateTo(target: String, isRoot: Boolean) {
        _routeFlow.emit(Command.Route(target, isRoot))
    }

    override suspend fun backTo(target: String?) {
        _routeFlow.emit(Command.Back(target))
    }

    override suspend fun openUrl(url: String) {
        _routeFlow.emit(Command.OpenUrl(url))
    }

    override suspend fun openApp(packageName: String, googlePlayUrl: String?, hmsUrl: String?) {
        _routeFlow.emit(Command.OpenApp(packageName, googlePlayUrl, hmsUrl))
    }

    override suspend fun call(phone: String) {
        _routeFlow.emit(Command.Call(phone))
    }

    companion object {
        private const val EXTRA_BUFFER_CAPACITY_VALUE = 1
        private const val REPLAY_VALUE = 1
    }
}
