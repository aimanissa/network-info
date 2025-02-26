package com.aimanissa.networkinfo.data.providers

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConnectionProviderImpl : ConnectionProvider {

    private val _activeWifiAccessPoint = MutableStateFlow(WifiAccessPoint.createEmpty())

    override val activeWifiAccessPoint: Flow<WifiAccessPoint> = _activeWifiAccessPoint.asStateFlow()

    override suspend fun updateActiveWifiAccessPoint(activeAccessPoint: WifiAccessPoint) {
        _activeWifiAccessPoint.emit(activeAccessPoint)
    }

    override suspend fun updateSignalStrengthDbm(rssi: Int) {
        _activeWifiAccessPoint.update { it.copy(signalStrengthDBm = rssi) }
    }
}