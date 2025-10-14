package com.example.poselens.domain.usecase

import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.EditPreset
import com.example.poselens.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for managing app preferences and settings
 */
class ManagePreferencesUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    // Theme Mode
    fun getThemeMode(): Flow<String> {
        return preferencesRepository.getThemeMode()
            .flowOn(dispatcher)
    }
    
    suspend fun setThemeMode(mode: String) {
        withContext(dispatcher) {
            preferencesRepository.setThemeMode(mode)
        }
    }
    
    // Edit Presets
    fun getSavedPresets(): Flow<List<EditPreset>> {
        return preferencesRepository.getSavedPresets()
            .flowOn(dispatcher)
    }
    
    suspend fun savePreset(preset: EditPreset) {
        withContext(dispatcher) {
            preferencesRepository.savePreset(preset)
        }
    }
    
    suspend fun deletePreset(presetId: String) {
        withContext(dispatcher) {
            preferencesRepository.deletePreset(presetId)
        }
    }
    
    // Onboarding
    fun isOnboardingComplete(): Flow<Boolean> {
        return preferencesRepository.isOnboardingComplete()
            .flowOn(dispatcher)
    }
    
    suspend fun completeOnboarding() {
        withContext(dispatcher) {
            preferencesRepository.setOnboardingComplete()
        }
    }
    
    // Pose Overlay
    fun getShowPoseOverlay(): Flow<Boolean> {
        return preferencesRepository.getShowPoseOverlay()
            .flowOn(dispatcher)
    }
    
    suspend fun setShowPoseOverlay(show: Boolean) {
        withContext(dispatcher) {
            preferencesRepository.setShowPoseOverlay(show)
        }
    }
    
    // Auto-save Images
    fun getAutoSaveImages(): Flow<Boolean> {
        return preferencesRepository.getAutoSaveImages()
            .flowOn(dispatcher)
    }
    
    suspend fun setAutoSaveImages(autoSave: Boolean) {
        withContext(dispatcher) {
            preferencesRepository.setAutoSaveImages(autoSave)
        }
    }
    
    // Image Quality
    fun getImageQuality(): Flow<Int> {
        return preferencesRepository.getImageQuality()
            .flowOn(dispatcher)
    }
    
    suspend fun setImageQuality(quality: Int) {
        withContext(dispatcher) {
            // Validate quality range
            val validQuality = quality.coerceIn(1, 100)
            preferencesRepository.setImageQuality(validQuality)
        }
    }
    
    // Analytics
    fun isAnalyticsEnabled(): Flow<Boolean> {
        return preferencesRepository.isAnalyticsEnabled()
            .flowOn(dispatcher)
    }
    
    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        withContext(dispatcher) {
            preferencesRepository.setAnalyticsEnabled(enabled)
        }
    }
    
    // Clear All
    suspend fun clearAllPreferences() {
        withContext(dispatcher) {
            preferencesRepository.clearAllPreferences()
        }
    }
}
