package com.aimanissa.base.navigation.domain.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.aimanissa.base.navigation.screens.ConnectionScreen
import com.aimanissa.features.connection.ui.ConnectionViewModel
import com.aimanissa.features.connection.ui.views.ConnectionRoute
import org.koin.androidx.compose.koinViewModel

class ConnectionScreenImpl : ConnectionScreen() {

    @Composable
    override fun NavGraphBuilder.Composable(
        backStackEntry: NavBackStackEntry,
        viewLifecycleOwner: LifecycleOwner
    ) {
        val viewModel = koinViewModel<ConnectionViewModel>()
        ConnectionRoute(viewModel)
    }
}
