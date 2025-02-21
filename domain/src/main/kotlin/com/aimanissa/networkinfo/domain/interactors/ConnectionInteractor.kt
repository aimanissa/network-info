package com.aimanissa.networkinfo.domain.interactors

import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.repos.ConnectionRepository
import kotlinx.coroutines.flow.Flow

interface ConnectionInteractor {
    suspend fun getWifiAccessPoints(): Flow<List<WifiAccessPoint>>
}

class ConnectionInteractorImpl(
    private val repository: ConnectionRepository
) : ConnectionInteractor  {

    override suspend fun getWifiAccessPoints(): Flow<List<WifiAccessPoint>> = repository.getWifiAccessPoints()
}