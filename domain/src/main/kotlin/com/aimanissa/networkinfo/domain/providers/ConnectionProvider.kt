package com.aimanissa.networkinfo.domain.providers

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import kotlinx.coroutines.flow.Flow

interface ConnectionProvider {

    val activeWifiAccessPoint: Flow<WifiAccessPoint>

    suspend fun updateActiveWifiAccessPoint(activeAccessPoint: WifiAccessPoint)

    suspend fun updateSignalStrengthDbm(rssi: Int)
}