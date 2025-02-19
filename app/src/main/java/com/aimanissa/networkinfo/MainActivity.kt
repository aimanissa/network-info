package com.aimanissa.networkinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator
import com.aimanissa.base.core.workers.SyncService
import com.aimanissa.base.navigation.domain.Navigator
import com.aimanissa.base.navigation.domain.ScreenProvider
import com.aimanissa.base.navigation.ui.NetInfoApp
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val screenProvider: ScreenProvider by inject()
    private val navigator: Navigator by inject()
    private val syncService: SyncService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val navController = NavHostController(this).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            navigatorProvider.addNavigator(DialogNavigator())
        }
        enableEdgeToEdge()
        setContent {
            NetInfoApp(screenProvider, navigator, navController, this)
        }
        lifecycle.addObserver(syncService)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(syncService)
    }
}
