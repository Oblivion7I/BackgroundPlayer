package com.afm.backgroundmusicplayer.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Player : Screen("player")
    object Settings : Screen("settings")
}