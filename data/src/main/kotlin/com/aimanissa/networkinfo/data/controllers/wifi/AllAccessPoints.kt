package com.aimanissa.networkinfo.data.controllers.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import com.aimanissa.base.extensions.resumeIfActive
import com.aimanissa.networkinfo.data.controllers.convertMhzToGhz
import com.aimanissa.networkinfo.data.controllers.formatSsid
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException

object AllAccessPoints {

    private var wifiManager: WifiManager? = null
    private var wifiResultsCallback: WifiManager.ScanResultsCallback? = null
    private var wifiResultsReceiver: BroadcastReceiver? = null

    private val isWifiReceiverRegistered = AtomicBoolean(false)
    private var timeLastWifiScan = 0L

    private const val WIFI_SCAN_TIMEOUT_MILLIS = 30000L

    suspend fun request(context: Context): List<WifiAccessPoint>? {
        return try {
            scanWifiAccessPoints(context)
        } catch (e: Exception) {
            Timber.e("AllAccessPoints request failed", e)
            null
        }
    }


    private suspend fun scanWifiAccessPoints(context: Context) =
        suspendCancellableCoroutine { continuation ->
            val currentTime = System.currentTimeMillis()

            if (currentTime > timeLastWifiScan + WIFI_SCAN_TIMEOUT_MILLIS) {
                wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

                val registration: () -> Unit

                val onAvailableResults = {
                    try {
                        continuation.resumeIfActive(getAccessPoints())
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    } finally {
                        unregisterReceiver(context)
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    wifiResultsCallback = object : WifiManager.ScanResultsCallback() {
                        override fun onScanResultsAvailable() {
                            onAvailableResults()
                        }
                    }

                    registration = {
                        wifiManager?.registerScanResultsCallback(
                            context.mainExecutor,
                            wifiResultsCallback as WifiManager.ScanResultsCallback
                        )
                    }
                } else {
                    wifiResultsReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context, intent: Intent) {
                            onAvailableResults()
                        }
                    }

                    registration = {
                        context.registerReceiver(
                            wifiResultsReceiver,
                            IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
                        )
                    }
                }

                registerReceiver(registration)

                @Suppress("DEPRECATION")
                if (wifiManager?.startScan() == true) {
                    timeLastWifiScan = currentTime
                }
            } else {
                continuation.resumeIfActive(null)
            }
        }

    private fun registerReceiver(registration: () -> Unit) {
        if (!isWifiReceiverRegistered.get()) {
            registration()
            isWifiReceiverRegistered.set(true)
        }
    }

    private fun unregisterReceiver(context: Context) {
        if (isWifiReceiverRegistered.get()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                wifiResultsCallback?.let {
                    wifiManager?.unregisterScanResultsCallback(it)
                }
            } else {
                wifiResultsReceiver?.let {
                    try {
                        context.unregisterReceiver(it)
                    } catch (e: IllegalArgumentException) {
                        Timber.e("AllAccessPoints unregister wi-fi result receiver", e)
                    }
                }
            }
            isWifiReceiverRegistered.set(false)
        }
    }

    @Suppress("MissingPermission")
    private fun getAccessPoints(): List<WifiAccessPoint>? {
        val wifiAccessPoints: MutableList<WifiAccessPoint> = mutableListOf()

        wifiManager?.scanResults?.forEach { result ->

            @Suppress("DEPRECATION")
            val ssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.wifiSsid.toString().formatSsid()
            } else {
                result.SSID
            }

            wifiAccessPoints.add(
                WifiAccessPoint(
                    macAddress = result.BSSID,
                    signalStrengthDBm = result.level,
                    ssid = ssid,
                    frequencyGhz = convertMhzToGhz(result.frequency),
                )
            )
        }

        return wifiAccessPoints.takeIf { it.isNotEmpty() }
    }
}