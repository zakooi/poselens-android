package com.example.poselens.domain.repository

import com.example.poselens.domain.model.EditPreset
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app preferences and settings
 */
interface PreferencesRepository {
    
    /**
     * Get theme mode preference
     * @return Flow emitting theme mode (light/dark/auto)
     */
    fun getThemeMode(): Flow<String>
    
    /**
     * Save theme mode preference
     * @param mode Theme mode to save
     */
    suspend fun setThemeMode(mode: String)
    
    /**
     * Get saved edit presets
     * @return Flow emitting list of custom presets
     */
    fun getSavedPresets(): Flow<List<EditPreset>>
    
    /**
     * Save a custom edit preset
     * @param preset Preset to save
     */
    suspend fun savePreset(preset: EditPreset)
    
    /**
     * Delete a custom preset
     * @param presetId Preset ID to delete
     */
    suspend fun deletePreset(presetId: String)
    
    /**
     * Get onboarding completion status
     * @return Flow emitting whether onboarding is complete
     */
    fun isOnboardingComplete(): Flow<Boolean>
    
    /**
     * Mark onboarding as complete
     */
    suspend fun setOnboardingComplete()
    
    /**
     * Get whether to show pose overlay in camera
     * @return Flow emitting overlay visibility preference
     */
    fun getShowPoseOverlay(): Flow<Boolean>
    
    /**
     * Set pose overlay visibility
     * @param show Whether to show overlay
     */
    suspend fun setShowPoseOverlay(show: Boolean)
    
    /**
     * Get whether to auto-save analyzed images
     * @return Flow emitting auto-save preference
     */
    fun getAutoSaveImages(): Flow<Boolean>
    
    /**
     * Set auto-save images preference
     * @param autoSave Whether to auto-save
     */
    suspend fun setAutoSaveImages(autoSave: Boolean)
    
    /**
     * Get image quality preference (1-100)
     * @return Flow emitting quality setting
     */
    fun getImageQuality(): Flow<Int>
    
    /**
     * Set image quality preference
     * @param quality Quality value (1-100)
     */
    suspend fun setImageQuality(quality: Int)
    
    /**
     * Get analytics enabled status
     * @return Flow emitting whether analytics is enabled
     */
    fun isAnalyticsEnabled(): Flow<Boolean>
    
    /**
     * Set analytics enabled status
     * @param enabled Whether to enable analytics
     */
    suspend fun setAnalyticsEnabled(enabled: Boolean)
    
    /**
     * Clear all preferences
     */
    suspend fun clearAllPreferences()
}
