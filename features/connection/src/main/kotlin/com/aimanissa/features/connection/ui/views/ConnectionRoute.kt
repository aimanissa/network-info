package com.aimanissa.features.connection.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.aimanissa.features.connection.ui.ConnectionViewModel

@Composable
fun ConnectionRoute(
    viewModel: ConnectionViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.viewState.collectAsState()
    ConnectionScreen(modifier, uiState)
}
