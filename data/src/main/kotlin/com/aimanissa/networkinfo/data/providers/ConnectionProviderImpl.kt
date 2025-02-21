package com.aimanissa.networkinfo.data.providers

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ConnectionProviderImpl : ConnectionProvider {

    private val _wifiAccessPoints =
        MutableSharedFlow<List<WifiAccessPoint>>(
            replay = FLOW_REPLAY_VALUE,
            extraBufferCapacity = FLOW_EXTRA_BUFFER_CAPACITY
        )

    override val wifiAccessPoints: Flow<List<WifiAccessPoint>> = _wifiAccessPoints.asSharedFlow()

    override suspend fun updateWifiAccessPoints(accessPoints: List<WifiAccessPoint>) {
        _wifiAccessPoints.emit(accessPoints)
    }

    companion object {
        private const val FLOW_REPLAY_VALUE = 1
        private const val FLOW_EXTRA_BUFFER_CAPACITY = 1
    }
}