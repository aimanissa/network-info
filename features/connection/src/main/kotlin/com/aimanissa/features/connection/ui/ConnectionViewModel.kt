package com.aimanissa.features.connection.ui

import androidx.lifecycle.viewModelScope
import com.aimanissa.base.core.platform.BaseViewModel
import com.aimanissa.base.core.platform.Event
import com.aimanissa.base.navigation.domain.Navigator
import com.aimanissa.base.navigation.domain.ScreenProvider
import com.aimanissa.networkinfo.domain.interactors.ConnectionInteractor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectionViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val navigator: Navigator,
    private val screenProvider: ScreenProvider,
    private val connectionInteractor: ConnectionInteractor
) : BaseViewModel<ViewState>(dispatcher) {

    init {
        viewModelScope.launch {
            connectionInteractor.getActiveWifiAccessPoint().collect { accessPoint ->
                processDataEvent(ConnectionDataEvent.OnWifiAccessPointReceived(accessPoint))
            }
        }
    }

    override fun initialViewState(): ViewState = ViewState()

    override fun reduce(event: Event): ViewState = when (event) {
        is ConnectionDataEvent -> dispatchConnectionDataEvent(event)
        else -> previousState
    }

    private fun dispatchConnectionDataEvent(event: ConnectionDataEvent): ViewState = when (event) {
        is ConnectionDataEvent.OnWifiAccessPointReceived -> {
            previousState.copy(wifiAccessPoint = event.wifiAccessPoint)
        }
    }
}
