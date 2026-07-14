package com.afm.backgroundmusicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.afm.backgroundmusicplayer.ui.navigation.Screen
import com.afm.backgroundmusicplayer.ui.screens.DashboardScreen
import com.afm.backgroundmusicplayer.ui.screens.PlayerScreen
import com.afm.backgroundmusicplayer.ui.screens.SettingsScreen
import com.afm.backgroundmusicplayer.ui.theme.BackgroundMusicPlayerTheme
import com.afm.backgroundmusicplayer.ui.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isDynamicColor by viewModel.isDynamicColor.collectAsState()

            BackgroundMusicPlayerTheme(
                darkTheme = isDarkMode,
                dynamicColor = isDynamicColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Player.route) {
                            PlayerScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(navController = navController, viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}