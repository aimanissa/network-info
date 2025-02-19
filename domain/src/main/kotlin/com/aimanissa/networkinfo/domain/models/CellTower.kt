package com.aimanissa.networkinfo.domain.models

sealed class CellTower {

  abstract fun isNotAvailable(): Boolean

  data class Gsm(
    val cellID: Int,
    val signalStrengthDB: Int,
    val locationAreaCode: Int,
    val timingAdvance: Int,
    val ageMs: Long
  ) : CellTower() {
    override fun isNotAvailable(): Boolean =
      arrayOf(cellID, locationAreaCode, timingAdvance).all { value ->
        value == Int.MAX_VALUE
      }
  }

  data class Lte(
   val cellID: Int,
    val signalStrengthDB: Int,
    val locationAreaCode: Int,
    val timingAdvance: Int,
    val ageMs: Long
  ) : CellTower() {
    override fun isNotAvailable(): Boolean =
      arrayOf(cellID, locationAreaCode, timingAdvance).all { value ->
        value == Int.MAX_VALUE
      }
  }

  data class Cdma(
   val cellID: Int,
    val signalStrengthDB: Int,
    val ageMs: Long
  ) : CellTower() {
    override fun isNotAvailable(): Boolean = cellID == Int.MAX_VALUE
  }

  data class Wcdma(
    val cellID: Int,
    val signalStrengthDB: Int,
    val locationAreaCode: Int,
    val ageMs: Long
  ) : CellTower() {
    override fun isNotAvailable(): Boolean =
      arrayOf(cellID, signalStrengthDB, locationAreaCode).all { value ->
        value == Int.MAX_VALUE
      }
  }

  data class Tdscdma(
    val cellID: Int,
    val signalStrengthDB: Int,
    val locationAreaCode: Int,
    val ageMs: Long
  ) : CellTower() {
    override fun isNotAvailable(): Boolean =
      arrayOf(cellID, signalStrengthDB, locationAreaCode).all { value ->
        value == Int.MAX_VALUE
      }
  }
}
