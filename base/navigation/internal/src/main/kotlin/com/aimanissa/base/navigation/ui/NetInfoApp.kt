package com.aimanissa.base.navigation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.aimanissa.base.navigation.domain.LocalAppProvider
import com.aimanissa.base.navigation.domain.Navigator
import com.aimanissa.base.navigation.domain.ScreenProvider
import com.aimanissa.base.theme.ui.NetInfoTheme

@Composable
fun NetInfoApp(
    screenProvider: ScreenProvider,
    navigator: Navigator,
    navController: NavHostController,
    viewLifecycleOwner: LifecycleOwner
) {
    NetInfoTheme {
        Box(Modifier.fillMaxSize()) {
            CompositionLocalProvider(
                LocalAppProvider provides screenProvider
            ) {
                NetInfoNavHost(
                    navController = navController,
                    navigator = navigator,
                    viewLifecycleOwner = viewLifecycleOwner
                )
            }
        }
    }
}
