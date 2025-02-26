package com.aimanissa.networkinfo.domain.models

data class WifiAccessPoint(
    val macAddress: String?,
    val signalStrengthDBm: Int?,
    val ssid: String?,
    val frequencyGhz: Double?,
) {
    companion object {
        fun createEmpty() = WifiAccessPoint(
            macAddress = null,
            signalStrengthDBm = null,
            ssid = null,
            frequencyGhz = null
        )
    }
}
