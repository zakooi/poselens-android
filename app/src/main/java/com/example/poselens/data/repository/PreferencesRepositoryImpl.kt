package com.example.poselens.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.poselens.domain.model.EditPreset
import com.example.poselens.domain.repository.PreferencesRepository
import com.example.poselens.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {
    
    private val gson = Gson()
    
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SAVED_PRESETS = stringPreferencesKey("saved_presets")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val SHOW_POSE_OVERLAY = booleanPreferencesKey("show_pose_overlay")
        val AUTO_SAVE_IMAGES = booleanPreferencesKey("auto_save_images")
        val IMAGE_QUALITY = intPreferencesKey("image_quality")
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
    }
    
    override fun getThemeMode(): Flow<String> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.THEME_MODE] ?: "auto"
            }
    }
    
    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
    
    override fun getSavedPresets(): Flow<List<EditPreset>> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                val json = preferences[PreferencesKeys.SAVED_PRESETS]
                if (json != null) {
                    val type = object : TypeToken<List<EditPreset>>() {}.type
                    gson.fromJson(json, type)
                } else {
                    emptyList()
                }
            }
    }
    
    override suspend fun savePreset(preset: EditPreset) {
        dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_PRESETS]
            val currentList = if (currentJson != null) {
                val type = object : TypeToken<List<EditPreset>>() {}.type
                gson.fromJson<List<EditPreset>>(currentJson, type).toMutableList()
            } else {
                mutableListOf()
            }
            
            currentList.add(preset)
            preferences[PreferencesKeys.SAVED_PRESETS] = gson.toJson(currentList)
        }
    }
    
    override suspend fun deletePreset(presetId: String) {
        dataStore.edit { preferences ->
            val currentJson = preferences[PreferencesKeys.SAVED_PRESETS]
            if (currentJson != null) {
                val type = object : TypeToken<List<EditPreset>>() {}.type
                val currentList = gson.fromJson<List<EditPreset>>(currentJson, type).toMutableList()
                currentList.removeAll { it.id == presetId }
                preferences[PreferencesKeys.SAVED_PRESETS] = gson.toJson(currentList)
            }
        }
    }
    
    override fun isOnboardingComplete(): Flow<Boolean> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.ONBOARDING_COMPLETE] ?: false
            }
    }
    
    override suspend fun setOnboardingComplete() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETE] = true
        }
    }
    
    override fun getShowPoseOverlay(): Flow<Boolean> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.SHOW_POSE_OVERLAY] ?: true
            }
    }
    
    override suspend fun setShowPoseOverlay(show: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_POSE_OVERLAY] = show
        }
    }
    
    override fun getAutoSaveImages(): Flow<Boolean> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.AUTO_SAVE_IMAGES] ?: false
            }
    }
    
    override suspend fun setAutoSaveImages(autoSave: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_SAVE_IMAGES] = autoSave
        }
    }
    
    override fun getImageQuality(): Flow<Int> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.IMAGE_QUALITY] ?: Constants.DEFAULT_IMAGE_QUALITY
            }
    }
    
    override suspend fun setImageQuality(quality: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IMAGE_QUALITY] = quality.coerceIn(1, 100)
        }
    }
    
    override fun isAnalyticsEnabled(): Flow<Boolean> {
        return dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { preferences ->
                preferences[PreferencesKeys.ANALYTICS_ENABLED] ?: true
            }
    }
    
    override suspend fun setAnalyticsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANALYTICS_ENABLED] = enabled
        }
    }
    
    override suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
