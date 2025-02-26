package com.aimanissa.networkinfo.data.controllers.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo.DetailedState
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.aimanissa.base.core.workers.LifecycleSubscriptionWorker
import com.aimanissa.networkinfo.data.controllers.convertMhzToGhz
import com.aimanissa.networkinfo.data.controllers.formatSsid
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WifiController(
    private val context: Context,
    private val provider: ConnectionProvider
) : LifecycleSubscriptionWorker() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiManager.RSSI_CHANGED_ACTION -> updateRssi(
                    intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0)
                )
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {
                    val state = intent.run {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            getParcelableExtra(
                                WifiManager.EXTRA_NEW_STATE,
                                SupplicantState::class.java
                            )
                        } else {
                            getParcelableExtra(WifiManager.EXTRA_NEW_STATE)
                        }
                    }

                    if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                        updateAccessPoint(getActiveAccessPoint())
                    }
                }
            }

        }
    }

    init {
        updateAccessPoint(getActiveAccessPoint())
    }

    @SuppressLint("HardwareIds")
    private fun getActiveAccessPoint(): WifiAccessPoint {
        try {
            val manager = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            if (manager.isWifiEnabled) {
                val wifiInfo = manager.connectionInfo
                if (wifiInfo != null) {
                    val state = WifiInfo.getDetailedStateOf(wifiInfo.supplicantState)
                    if (state == DetailedState.CONNECTED || state == DetailedState.OBTAINING_IPADDR) {
                        val ssid = wifiInfo.ssid.also {
                            if (it.startsWith("\"") && it.endsWith("\"")) it.formatSsid()
                        }
//                        wifiInfo.maxSupportedRxLinkSpeedMbps
//                        wifiInfo.maxSupportedTxLinkSpeedMbps
//                        wifiInfo.linkSpeed
//                        wifiInfo.wifiStandard
//                        wifiInfo.ipAddress


                        return WifiAccessPoint(
                            ssid = ssid,
                            macAddress = wifiInfo.macAddress,
                            signalStrengthDBm = wifiInfo.rssi,
                            frequencyGhz = convertMhzToGhz(wifiInfo.frequency)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            return WifiAccessPoint.createEmpty()
        }
        return WifiAccessPoint.createEmpty()
    }

    suspend fun getAllAccessPoints(): List<WifiAccessPoint> {
        val result = scope.async {
            AllAccessPoints.request(context)
        }.await()

        return result ?: emptyList()
    }

    fun updateAccessPoint(accessPoint: WifiAccessPoint) {
        scope.launch {
            provider.updateActiveWifiAccessPoint(accessPoint)
        }
    }

    fun updateRssi(rssi: Int) {
        scope.launch {
            provider.updateSignalStrengthDbm(rssi)
        }
    }

    override fun subscribe() {
        context.registerReceiver(
            receiver,
            IntentFilter(
                WifiManager.SUPPLICANT_STATE_CHANGED_ACTION,
                WifiManager.RSSI_CHANGED_ACTION
            )
        )
    }

    override fun unsubscribe() {
        context.unregisterReceiver(receiver)
    }
}
