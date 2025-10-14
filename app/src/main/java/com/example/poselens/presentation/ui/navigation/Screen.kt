package com.example.poselens.presentation.ui.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Analyze : Screen("analyze/{imageUri}") {
        fun createRoute(imageUri: String) = "analyze/$imageUri"
    }
    object Edit : Screen("edit/{imageUri}") {
        fun createRoute(imageUri: String) = "edit/$imageUri"
    }
    object Templates : Screen("templates")
    object Settings : Screen("settings")
}
