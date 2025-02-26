package com.aimanissa.networkinfo.data.repos

import com.aimanissa.networkinfo.data.controllers.wifi.WifiController
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import com.aimanissa.networkinfo.domain.repos.ConnectionRepository
import kotlinx.coroutines.flow.Flow

class ConnectionRepositoryImpl(
    private val provider: ConnectionProvider,
    private val wifiController: WifiController
) : ConnectionRepository {

    override suspend fun getActiveWifiAccessPoint(): Flow<WifiAccessPoint> =
        provider.activeWifiAccessPoint

    override suspend fun getWifiAccessPoints(): List<WifiAccessPoint> =
        wifiController.getAllAccessPoints()
}