package com.aimanissa.base.navigation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aimanissa.base.extensions.call
import com.aimanissa.base.extensions.openApp
import com.aimanissa.base.extensions.openUrl
import com.aimanissa.base.navigation.domain.Command
import com.aimanissa.base.navigation.domain.KEY_NAVIGATOR
import com.aimanissa.base.navigation.domain.LocalAppProvider
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.aimanissa.base.navigation.domain.Navigator

@Composable
fun NetInfoNavHost(
    navigator: Navigator,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewLifecycleOwner: LifecycleOwner
) {
   val connectionScreen = LocalAppProvider.current.connectionScreen()


    LaunchedEffect(KEY_NAVIGATOR) {
        navigator.routeFlow.onEach { route ->
            navController.navigate(route)
        }.launchIn(this)
    }

    NavHost(
        navController = navController,
        startDestination = connectionScreen.destination(),
        modifier = modifier
    ) {
        with(connectionScreen) {
            composable(viewLifecycleOwner)
        }
    }
}

private fun NavHostController.navigate(command: Command) {
    when (command) {
        is Command.Route -> {
            navigate(command.screen) {
                if (command.isRoot) {
                    currentBackStackEntry?.let { entry ->
                        popUpTo(entry.destination.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
        is Command.Back -> {
            popBackStack()
        }
        is Command.OpenUrl -> {
            context.openUrl(command.url)
        }
        is Command.OpenApp -> {
            context.openApp(command.packageName, command.googlePlayUrl, command.hmsUrl)
        }
        is Command.Call -> {
            context.call(command.phone)
        }
    }
}
