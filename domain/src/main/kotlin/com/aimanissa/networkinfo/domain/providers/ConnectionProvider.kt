package com.aimanissa.networkinfo.domain.providers

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import kotlinx.coroutines.flow.Flow

interface ConnectionProvider {

    val wifiAccessPoints: Flow<List<WifiAccessPoint>>

    suspend fun updateWifiAccessPoints(accessPoints: List<WifiAccessPoint>)
}