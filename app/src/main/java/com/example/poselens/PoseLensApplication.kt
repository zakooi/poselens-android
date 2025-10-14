package com.example.poselens

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * PoseLens Application class
 * Entry point for Hilt dependency injection
 */
@HiltAndroidApp
class PoseLensApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize app-wide configurations
        initializeApp()
    }
    
    private fun initializeApp() {
        // TODO: Initialize crash reporting (e.g., Firebase Crashlytics)
        // TODO: Initialize analytics
        // TODO: Setup notification channels
        // TODO: Configure ML Kit models download
    }
}
