package com.aimanissa.networkinfo.data.controllers.cell

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfo
import android.telephony.CellInfoCdma
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoTdscdma
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.CellInfoCallback
import com.aimanissa.base.extensions.resumeIfActive
import com.aimanissa.networkinfo.domain.models.CellTower
import com.aimanissa.networkinfo.domain.models.MobileNetwork
import com.aimanissa.networkinfo.domain.providers.ConnectionProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

class CellController(
    private val context: Context,
    private val provider: ConnectionProvider
) {

    @Suppress("MissingPermission")
    private suspend fun requestMobileNetwork() = suspendCancellableCoroutine { continuation ->
        val hasTelephony =
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

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

    companion object {
        private const val MCC_DIGITS = 3
        private const val MNC_DIGITS = 2
    }
}