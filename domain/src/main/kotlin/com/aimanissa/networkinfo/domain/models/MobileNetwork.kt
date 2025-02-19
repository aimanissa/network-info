package com.aimanissa.networkinfo.domain.models

data class MobileNetwork(
  val homeMobileCountryCode: Int,
  val homeMobileNetworkCode: Int,
  val cellTowers: List<CellTower>?
)