package com.aimanissa.features.connection.ui

import com.aimanissa.base.core.platform.BaseViewState
import com.aimanissa.base.core.platform.DataEvent
import com.aimanissa.features.connection.R
import com.aimanissa.networkinfo.domain.models.WifiAccessPoint

private val wifiDetailsTitle = listOf(
    R.string.txt_tx_max_speed,
    R.string.txt_rx_link_speed,
    R.string.txt_link_speed,
    R.string.txt_frequency,
    R.string.txt_channel,
    R.string.txt_wifi_standard,
    R.string.txt_gateway,
    R.string.txt_ip,
    R.string.txt_public_ip,
    R.string.txt_dns,
    R.string.txt_net_mask,
)


data class ViewState(
    val wifiAccessPoint: WifiAccessPoint = WifiAccessPoint.createEmpty()
) : BaseViewState() {
    val titles = wifiDetailsTitle
}

sealed interface ConnectionDataEvent : DataEvent {
    data class OnWifiAccessPointReceived(val wifiAccessPoint: WifiAccessPoint) : ConnectionDataEvent
}