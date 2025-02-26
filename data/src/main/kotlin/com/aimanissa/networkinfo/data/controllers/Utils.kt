package com.aimanissa.networkinfo.data.controllers

import java.math.BigDecimal
import java.math.RoundingMode

internal fun String.formatSsid() = this.replace("\"", "")

internal fun convertMhzToGhz(frequency: Int) = BigDecimal(frequency / 1000.00)
    .setScale(1, RoundingMode.FLOOR).toDouble()