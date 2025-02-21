package com.aimanissa.networkinfo.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import com.aimanissa.base.extensions.resumeIfActive
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException
import kotlin.math.sign

class WifiController(
  private val context: Context,
  private val provider: ConnectionProvider
) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private var wifiManager: WifiManager? = null
  private var wifiResultsCallback: WifiManager.ScanResultsCallback? = null
  private var wifiResultsReceiver: BroadcastReceiver? = null

  private val isWifiReceiverRegistered = AtomicBoolean(false)
  private var timeLastWifiScan = 0L

  init {
    scope.launch {
      requestWifiAccessPoints()?.let {
        provider.updateWifiAccessPoints(it)
      }
    }
  }

  private suspend fun requestWifiAccessPoints(): List<WifiAccessPoint>? {
    val wifiAccessPoints = startScanWifiAccessPoints()
    unregisterWifiScanResultsReceiver()
    return wifiAccessPoints
  }


  private suspend fun startScanWifiAccessPoints() =
    suspendCancellableCoroutine { continuation ->
      val currentTime = System.currentTimeMillis()

      if (currentTime > timeLastWifiScan + WIFI_SCAN_TIMEOUT_MILLIS) {
        wifiManager = context.applicationContext.getSystemService(
          Context.WIFI_SERVICE
        ) as WifiManager

        val registration: () -> Unit

        val onResult: (List<WifiAccessPoint>) -> Unit = { accessPoints ->
          if (accessPoints.isNotEmpty()) {
            continuation.resumeIfActive(accessPoints)
          } else {
            continuation.resumeIfActive(null)
          }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          wifiResultsCallback = object : WifiManager.ScanResultsCallback() {
            override fun onScanResultsAvailable() {
              try {
                onResult(getWifiAccessPoints())
              } catch (e: Exception) {
                continuation.resumeWithException(e)
              }
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
              try {
                onResult(getWifiAccessPoints())
              } catch (e: Exception) {
                continuation.resumeWithException(e)
              }
            }
          }

          registration = {
            context.registerReceiver(
              wifiResultsReceiver,
              IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            )
          }
        }

        registerWifiScanResultsReceiver(registration)

        @Suppress("DEPRECATION")
        if (wifiManager?.startScan() == true) {
          timeLastWifiScan = currentTime
        }
      } else {
        continuation.resumeIfActive(null)
      }
    }

  private fun registerWifiScanResultsReceiver(registration: () -> Unit) {
    if (!isWifiReceiverRegistered.get()) {
      registration()
      isWifiReceiverRegistered.set(true)
    }
  }

  private fun unregisterWifiScanResultsReceiver() {
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
            Timber.e("WifiController unregister wi-fi result receiver", e)
          }
        }
      }
      isWifiReceiverRegistered.set(false)
    }
  }

  @Suppress("MissingPermission")
  private fun getWifiAccessPoints(): List<WifiAccessPoint> {
    val wifiAccessPoints: MutableList<WifiAccessPoint> = mutableListOf()

    wifiManager?.scanResults?.forEach { result ->

      @Suppress("DEPRECATION")
      val ssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        result.wifiSsid.toString().replace("\"", "")
      } else {
        result.SSID
      }

      val frequencyGhz = BigDecimal(result.frequency / 1000.00)
        .setScale(1, RoundingMode.FLOOR).toDouble()

      val lastSeenMs = TimeUnit.MICROSECONDS.toMillis(result.timestamp).let { value ->
        if (value.sign == 1) value.toInt() else 0
      }

      wifiAccessPoints.add(
        WifiAccessPoint(
          macAddress = result.BSSID,
          signalStrengthDBm = result.level,
          ssid = ssid,
          frequencyGhz = frequencyGhz,
          lastSeenMs = lastSeenMs
        )
      )
    }

    return wifiAccessPoints
  }

  companion object {
    private const val WIFI_SCAN_TIMEOUT_MILLIS = 30000L
  }
}
