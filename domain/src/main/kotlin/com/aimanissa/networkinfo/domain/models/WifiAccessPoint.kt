package com.aimanissa.networkinfo.domain.models

data class WifiAccessPoint(
  val macAddress: String,
  val signalStrengthDBm: Int,
  val ssid: String,
  val frequencyGhz: Double,
  val lastSeenMs: Int
)
