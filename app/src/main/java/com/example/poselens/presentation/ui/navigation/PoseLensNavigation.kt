package com.example.poselens.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poselens.presentation.ui.screens.home.HomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Main navigation graph for PoseLens
 */
@Composable
fun PoseLensNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onCameraClick = {
                    navController.navigate(Screen.Camera.route)
                },
                onGalleryImageSelected = { imageUri ->
                    val encodedUri = URLEncoder.encode(
                        imageUri,
                        StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate(Screen.Analyze.createRoute(encodedUri))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // Camera Screen (placeholder for now)
        composable(Screen.Camera.route) {
            // TODO: Implement CameraScreen in Phase 2
            HomeScreen(
                onCameraClick = {},
                onGalleryImageSelected = {},
                onSettingsClick = {}
            )
        }
        
        // Analyze Screen (placeholder for now)
        composable(
            route = Screen.Analyze.route,
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUri")
            val imageUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
            
            // TODO: Implement AnalyzeScreen
            HomeScreen(
                onCameraClick = {},
                onGalleryImageSelected = {},
                onSettingsClick = {}
            )
        }
        
        // Edit Screen (placeholder for now)
        composable(
            route = Screen.Edit.route,
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUri")
            val imageUri = URLDecoder.decode(encodedUri, StandardCharsets.UTF_8.toString())
            
            // TODO: Implement EditScreen
            HomeScreen(
                onCameraClick = {},
                onGalleryImageSelected = {},
                onSettingsClick = {}
            )
        }
        
        // Templates Screen (placeholder for now)
        composable(Screen.Templates.route) {
            // TODO: Implement TemplatesScreen in Phase 2
            HomeScreen(
                onCameraClick = {},
                onGalleryImageSelected = {},
                onSettingsClick = {}
            )
        }
        
        // Settings Screen (placeholder for now)
        composable(Screen.Settings.route) {
            // TODO: Implement SettingsScreen
            HomeScreen(
                onCameraClick = {},
                onGalleryImageSelected = {},
                onSettingsClick = {}
            )
        }
    }
}
