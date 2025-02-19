package com.aimanissa.networkinfo.domain.models

data class LocationData(
  val wifiAccessPoints: List<WifiAccessPoint>?,
  val mobileNetwork: MobileNetwork?
)
