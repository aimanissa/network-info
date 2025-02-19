package com.aimanissa.features.connection.ui

import com.aimanissa.base.core.platform.BaseViewModel
import com.aimanissa.base.core.platform.Event
import com.aimanissa.base.navigation.domain.Navigator
import com.aimanissa.base.navigation.domain.ScreenProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("LongParameterList")
class ConnectionViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val navigator: Navigator,
    private val screenProvider: ScreenProvider,
) : BaseViewModel<ViewState>(dispatcher) {

    override fun initialViewState(): ViewState = ViewState()

    override fun reduce(event: Event): ViewState = ViewState()

    private fun dispatchSplashDataEvent(event: SplashDataEvent): ViewState = ViewState()


    private fun navigateToNextScreen() {
//        viewModelScope.launch(dispatcher) {
//            navigator.navigateTo(screen, isRoot = true)
//        }
    }
}
