package com.aimanissa.networkinfo.data

import android.Manifest
import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoTdscdma
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.CellInfoCallback
import androidx.core.content.ContextCompat
import com.aimanissa.networkinfo.domain.models.CellTower
import com.aimanissa.networkinfo.domain.models.LocationData
import com.aimanissa.networkinfo.domain.models.MobileNetwork
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint
import kotlinx.coroutines.CancellableContinuation
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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.sign

class NetworkAccessPoints(private val context: Context) {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  private var wifiManager: WifiManager? = null
  private var wifiResultsCallback: WifiManager.ScanResultsCallback? = null
  private var wifiResultsReceiver: BroadcastReceiver? = null

  private val isWifiReceiverRegistered = AtomicBoolean(false)
  private var timeLastWifiScan = 0L

  private var locationData: LocationData? = null

  fun request(onComplete: (data: LocationData?, exception: Exception?) -> Unit) {
    scope.launch {
      try {
        if (hasLocationPermission()) {
          val wifiAccessPoints = requestWifiAccessPoints()
          val mobileNetwork = requestMobileNetwork()

          if (!wifiAccessPoints.isNullOrEmpty() || mobileNetwork != null) {
            locationData = LocationData(wifiAccessPoints, mobileNetwork)
          }
        }
        onComplete(locationData, null)
      } catch (exception: Exception) {
        onComplete(null, exception)
      }
    }
  }

  private suspend fun requestWifiAccessPoints(): List<WifiAccessPoint>? {
    val wifiAccessPoints = startScanWifiAccessPoints()
    unregisterWifiScanResultsReceiver()
    return wifiAccessPoints
  }

  @Suppress("MissingPermission")
  private suspend fun requestMobileNetwork() =
    suspendCancellableCoroutine { continuation ->
      val hasTelephony = context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

      if (hasTelephony) {
        val telephonyManager = context.getSystemService(
          Context.TELEPHONY_SERVICE
        ) as TelephonyManager

        val code = telephonyManager.networkOperator
        val countryCode = code.take(MCC_DIGITS).toIntOrNull() ?: 0
        val networkCode = code.takeLast(MNC_DIGITS).toIntOrNull() ?: 0

        val onResult: (List<CellTower>?) -> Unit = { cellTowers ->
          if (cellTowers.isNullOrEmpty()) {
            continuation.resumeIfActive(null)
          } else {
            continuation.resumeIfActive(
              MobileNetwork(
                homeMobileCountryCode = countryCode,
                homeMobileNetworkCode = networkCode,
                cellTowers = cellTowers
              )
            )
          }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          telephonyManager.requestCellInfoUpdate(
            context.mainExecutor,
            object : CellInfoCallback() {
              override fun onCellInfo(activeCellInfo: MutableList<CellInfo>) {
                try {
                  onResult(convertCellsInfoToCellTowers(activeCellInfo))
                } catch (e: Exception) {
                  continuation.resumeWithException(e)
                }
              }
            }
          )
        } else {
          try {
            onResult(telephonyManager.allCellInfo?.let { convertCellsInfoToCellTowers(it) })
          } catch (e: Exception) {
            continuation.resumeWithException(e)
          }
        }
      } else {
        continuation.resumeIfActive(null)
      }
    }

  @TargetApi(Build.VERSION_CODES.R)
  private fun convertCellsInfoToCellTowers(cellsInfo: List<CellInfo>): List<CellTower> {
    val cellTowers: MutableList<CellTower> = mutableListOf()
    val sdkVersion = Build.VERSION.SDK_INT

    cellsInfo.forEach { cellInfo ->
      @Suppress("DEPRECATION")
      val ageMs = if (sdkVersion >= Build.VERSION_CODES.R) {
        cellInfo.timestampMillis
      } else {
        TimeUnit.NANOSECONDS.toMillis(cellInfo.timeStamp)
      }

      when (cellInfo) {
        is CellInfoGsm -> {
          val timingAdvance = if (sdkVersion >= Build.VERSION_CODES.O) {
            cellInfo.cellSignalStrength.timingAdvance
          } else {
            0
          }

          cellTowers.add(
            CellTower.Gsm(
              cellID = cellInfo.cellIdentity.cid,
              signalStrengthDB = cellInfo.cellSignalStrength.dbm,
              locationAreaCode = cellInfo.cellIdentity.lac,
              timingAdvance = timingAdvance,
              ageMs = ageMs
            )
          )
        }

        is CellInfoLte -> {
          cellTowers.add(
            CellTower.Lte(
              cellID = cellInfo.cellIdentity.ci,
              signalStrengthDB = cellInfo.cellSignalStrength.dbm,
              locationAreaCode = cellInfo.cellIdentity.tac,
              timingAdvance = cellInfo.cellSignalStrength.timingAdvance,
              ageMs = ageMs
            )
          )
        }

        is CellInfoWcdma -> {
          cellTowers.add(
            CellTower.Wcdma(
              cellID = cellInfo.cellIdentity.cid,
              signalStrengthDB = cellInfo.cellSignalStrength.dbm,
              locationAreaCode = cellInfo.cellIdentity.lac,
              ageMs = ageMs
            )
          )
        }

        is CellInfoCdma -> {
          cellTowers.add(
            CellTower.Cdma(
              cellID = cellInfo.cellIdentity.basestationId,
              signalStrengthDB = cellInfo.cellSignalStrength.dbm,
              ageMs = ageMs
            )
          )
        }

        else -> {
          if (sdkVersion >= Build.VERSION_CODES.Q && cellInfo is CellInfoTdscdma) {
            cellTowers.add(
              CellTower.Tdscdma(
                cellID = cellInfo.cellIdentity.cid,
                signalStrengthDB = cellInfo.cellSignalStrength.dbm,
                locationAreaCode = cellInfo.cellIdentity.lac,
                ageMs = ageMs
              )
            )
          }
        }
      }
    }
    return cellTowers.filterNot(CellTower::isNotAvailable)
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
            Timber.e("DeviceData unregister wi-fi result receiver", e)
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

  private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
  ) == PackageManager.PERMISSION_GRANTED

  private fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (isActive) {
      resume(value)
    }
  }

  companion object {
    private const val MCC_DIGITS = 3
    private const val MNC_DIGITS = 2
    private const val WIFI_SCAN_TIMEOUT_MILLIS = 30000L
  }
}
