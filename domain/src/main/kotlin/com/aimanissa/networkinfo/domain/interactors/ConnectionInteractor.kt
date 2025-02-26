package com.aimanissa.networkinfo.domain.interactors

import arrow.core.Either
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.repos.ConnectionRepository
import com.aimanissa.networkinfo.domain.utils.attempt
import kotlinx.coroutines.flow.Flow

interface ConnectionInteractor {
    suspend fun getActiveWifiAccessPoint(): Flow<WifiAccessPoint>

    suspend fun getWifiAccessPoints(): Either<Throwable, List<WifiAccessPoint>>
}

class ConnectionInteractorImpl(
    private val repository: ConnectionRepository
) : ConnectionInteractor {

    override suspend fun getActiveWifiAccessPoint(): Flow<WifiAccessPoint> =
        repository.getActiveWifiAccessPoint()

    override suspend fun getWifiAccessPoints(): Either<Throwable, List<WifiAccessPoint>> = attempt {
        repository.getWifiAccessPoints()
    }
}