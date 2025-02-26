package com.aimanissa.networkinfo.domain.repos

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import kotlinx.coroutines.flow.Flow

interface ConnectionRepository {

    suspend fun getActiveWifiAccessPoint(): Flow<WifiAccessPoint>

    suspend fun getWifiAccessPoints(): List<WifiAccessPoint>
}