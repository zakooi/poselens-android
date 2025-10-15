package com.example.poselens.presentation.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.poselens.presentation.ui.screens.analyze.AnalyzeScreen
import com.example.poselens.presentation.ui.screens.edit.EditScreen
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
        
        // Analyze Screen
        composable(
            route = Screen.Analyze.route,
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUri")
            val imageUri = encodedUri?.let { 
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString()).toUri()
            }
            
            AnalyzeScreen(
                imageUri = imageUri,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditClick = { uri ->
                    val encodedEditUri = URLEncoder.encode(
                        uri.toString(),
                        StandardCharsets.UTF_8.toString()
                    )
                    navController.navigate(Screen.Edit.createRoute(encodedEditUri))
                }
            )
        }
        
        // Edit Screen
        composable(
            route = Screen.Edit.route,
            arguments = listOf(
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("imageUri")
            val imageUri = encodedUri?.let { 
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString()).toUri()
            }
            
            EditScreen(
                imageUri = imageUri,
                onNavigateBack = {
                    navController.popBackStack()
                }
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
