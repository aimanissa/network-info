package com.aimanissa.base.extensions

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ANIMATE_DELAY = 100L

@OptIn(ExperimentalMaterialApi::class)
fun ModalBottomSheetState.hide(scope: CoroutineScope, action: () -> Unit) {
    scope.launch(Dispatchers.Main.immediate) {
        hide()
        delay(ANIMATE_DELAY)
        action()
    }
}

@OptIn(ExperimentalMaterialApi::class)
fun ModalBottomSheetState.expand(scope: CoroutineScope) {
    scope.launch(Dispatchers.Main.immediate) {
        show()
    }
}
