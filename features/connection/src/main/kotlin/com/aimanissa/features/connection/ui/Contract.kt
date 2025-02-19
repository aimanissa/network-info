package com.aimanissa.features.connection.ui

import com.aimanissa.base.core.platform.BaseViewState
import com.aimanissa.base.core.platform.DataEvent
import com.aimanissa.features.connection.R

//enum class WifiDetails(@StringRes val text: Int) {
//    TxRxSpeed(R.string.txt_tx_rx_speed),
//    LinkSpeed(R.string.txt_link_speed),
//    Frequency(R.string.txt_frequency),
//    Channel(R.string.txt_channel),
//    Standard(R.string.txt_wifi_standard),
//    Gateway(R.string.txt_gateway),
//    Ip(R.string.txt_ip),
//    PublicIp(R.string.txt_public_ip),
//    Dns(R.string.txt_dns),
//    NetMask(R.string.txt_net_mask),
//}

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
    val wifiDetails: List<Any>? = null
) : BaseViewState() {
    val titles = wifiDetailsTitle
}

sealed interface SplashDataEvent : DataEvent {
    object StartTimer : SplashDataEvent
    object StopTimer : SplashDataEvent
    object StartDelayFowShowLogo : SplashDataEvent
    object ShowLogo : SplashDataEvent
}
