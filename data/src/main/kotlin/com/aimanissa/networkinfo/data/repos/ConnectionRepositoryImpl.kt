package com.aimanissa.networkinfo.data.repos

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import com.aimanissa.networkinfo.domain.repos.ConnectionRepository
import kotlinx.coroutines.flow.Flow

class ConnectionRepositoryImpl(
    private val provider: ConnectionProvider
) : ConnectionRepository {

    override suspend fun getWifiAccessPoints(): Flow<List<WifiAccessPoint>> = provider.wifiAccessPoints
}