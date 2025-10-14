package com.example.poselens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.poselens.presentation.ui.navigation.PoseLensNavigation
import com.example.poselens.presentation.ui.theme.PoseLensTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity - Entry point for the PoseLens app
 * Uses Jetpack Compose for UI
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            PoseLensTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PoseLensNavigation()
                }
            }
        }
    }
}
