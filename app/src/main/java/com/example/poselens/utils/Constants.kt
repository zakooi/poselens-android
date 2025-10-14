package com.example.poselens.utils

/**
 * Constants used throughout the app
 */
object Constants {
    
    // API Configuration
    const val API_BASE_URL = "https://api.poselens.app/v1/"
    const val API_TIMEOUT_SECONDS = 30L
    
    // Database Configuration
    const val DATABASE_NAME = "poselens_database"
    const val DATABASE_VERSION = 1
    
    // DataStore Configuration
    const val PREFERENCES_NAME = "poselens_preferences"
    
    // ML Kit Configuration
    const val POSE_DETECTION_CONFIDENCE_THRESHOLD = 0.5f
    const val SCENE_CLASSIFICATION_THRESHOLD = 0.7f
    const val MAX_LANDMARKS = 33
    
    // Image Processing
    const val MAX_IMAGE_SIZE = 1920 // pixels
    const val IMAGE_QUALITY = 90 // 0-100
    const val THUMBNAIL_SIZE = 256
    
    // Camera Configuration
    const val CAMERA_ASPECT_RATIO_4_3 = 4.0 / 3.0
    const val CAMERA_ASPECT_RATIO_16_9 = 16.0 / 9.0
    
    // Pose Template Categories
    const val TEMPLATE_CATEGORY_ALL = "all"
    const val TEMPLATE_CATEGORY_CASUAL = "casual"
    const val TEMPLATE_CATEGORY_FASHION = "fashion"
    const val TEMPLATE_CATEGORY_PORTRAIT = "portrait"
    const val TEMPLATE_CATEGORY_STREET = "street"
    
    // Edit Preset IDs
    const val PRESET_GOLDEN_HOUR = "preset_golden_hour"
    const val PRESET_NATURE_FRESH = "preset_nature_fresh"
    const val PRESET_INDOOR_COZY = "preset_indoor_cozy"
    const val PRESET_URBAN_STREET = "preset_urban_street"
    const val PRESET_VINTAGE_FILM = "preset_vintage_film"
    
    // Preferences Keys
    object PreferenceKeys {
        const val THEME_MODE = "theme_mode"
        const val DYNAMIC_COLORS = "dynamic_colors"
        const val GRID_OVERLAY = "grid_overlay"
        const val CAMERA_FLASH = "camera_flash"
        const val AUTO_SAVE = "auto_save"
        const val PROCESSING_MODE = "processing_mode" // on-device vs cloud
    }
    
    // Intent Actions
    const val ACTION_ANALYZE_IMAGE = "com.example.poselens.ANALYZE_IMAGE"
    const val ACTION_OPEN_CAMERA = "com.example.poselens.OPEN_CAMERA"
    
    // Notification Channels
    const val NOTIFICATION_CHANNEL_DEFAULT = "poselens_default"
    const val NOTIFICATION_CHANNEL_ML_DOWNLOAD = "poselens_ml_download"
    
    // File Provider Authority
    const val FILE_PROVIDER_AUTHORITY = "com.example.poselens.fileprovider"
    
    // Analytics Events
    object AnalyticsEvents {
        const val PHOTO_ANALYZED = "photo_analyzed"
        const val POSE_SUGGESTED = "pose_suggested"
        const val PRESET_APPLIED = "preset_applied"
        const val TEMPLATE_VIEWED = "template_viewed"
        const val PHOTO_SAVED = "photo_saved"
        const val PHOTO_SHARED = "photo_shared"
    }
    
    // Error Messages
    object ErrorMessages {
        const val GENERIC_ERROR = "Something went wrong. Please try again."
        const val NETWORK_ERROR = "Network connection error. Please check your internet."
        const val PERMISSION_DENIED = "Permission denied. Please grant the required permissions."
        const val IMAGE_LOAD_ERROR = "Failed to load image. Please try another one."
        const val ML_MODEL_ERROR = "ML model not available. Please check your connection."
        const val CAMERA_ERROR = "Camera initialization failed. Please try again."
    }
}
