package com.example.poselens.presentation.ui.screens.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poselens.domain.model.EditPreset
import com.example.poselens.domain.model.ImageAdjustments
import com.example.poselens.domain.usecase.ApplyEditPresetUseCase
import com.example.poselens.domain.usecase.GetEditPresetsUseCase
import com.example.poselens.domain.usecase.SaveCustomPresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Edit Screen
 * 
 * Responsibilities:
 * - Manage image editing state
 * - Handle adjustment parameters
 * - Apply presets
 * - Save custom presets
 * - Generate before/after comparison
 */
@HiltViewModel
class EditViewModel @Inject constructor(
    private val applyEditPresetUseCase: ApplyEditPresetUseCase,
    private val getEditPresetsUseCase: GetEditPresetsUseCase,
    private val saveCustomPresetUseCase: SaveCustomPresetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditUiState>(EditUiState.Idle)
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    private val _adjustments = MutableStateFlow(ImageAdjustments.default())
    val adjustments: StateFlow<ImageAdjustments> = _adjustments.asStateFlow()

    private val _presets = MutableStateFlow<List<EditPreset>>(emptyList())
    val presets: StateFlow<List<EditPreset>> = _presets.asStateFlow()

    private var originalImageUri: Uri? = null

    /**
     * Initialize edit screen with image
     */
    fun initializeWithImage(imageUri: Uri) {
        originalImageUri = imageUri
        _uiState.value = EditUiState.Ready(imageUri)
        loadPresets()
    }

    /**
     * Load available presets
     */
    private fun loadPresets() {
        viewModelScope.launch {
            try {
                val presetList = getEditPresetsUseCase()
                _presets.value = presetList
            } catch (e: Exception) {
                // Log error but don't block UI
            }
        }
    }

    /**
     * Update brightness adjustment
     */
    fun updateBrightness(value: Float) {
        _adjustments.value = _adjustments.value.copy(brightness = value)
        applyAdjustments()
    }

    /**
     * Update contrast adjustment
     */
    fun updateContrast(value: Float) {
        _adjustments.value = _adjustments.value.copy(contrast = value)
        applyAdjustments()
    }

    /**
     * Update saturation adjustment
     */
    fun updateSaturation(value: Float) {
        _adjustments.value = _adjustments.value.copy(saturation = value)
        applyAdjustments()
    }

    /**
     * Update sharpness adjustment
     */
    fun updateSharpness(value: Float) {
        _adjustments.value = _adjustments.value.copy(sharpness = value)
        applyAdjustments()
    }

    /**
     * Update temperature adjustment
     */
    fun updateTemperature(value: Float) {
        _adjustments.value = _adjustments.value.copy(temperature = value)
        applyAdjustments()
    }

    /**
     * Update tint adjustment
     */
    fun updateTint(value: Float) {
        _adjustments.value = _adjustments.value.copy(tint = value)
        applyAdjustments()
    }

    /**
     * Update exposure adjustment
     */
    fun updateExposure(value: Float) {
        _adjustments.value = _adjustments.value.copy(exposure = value)
        applyAdjustments()
    }

    /**
     * Update highlights adjustment
     */
    fun updateHighlights(value: Float) {
        _adjustments.value = _adjustments.value.copy(highlights = value)
        applyAdjustments()
    }

    /**
     * Update shadows adjustment
     */
    fun updateShadows(value: Float) {
        _adjustments.value = _adjustments.value.copy(shadows = value)
        applyAdjustments()
    }

    /**
     * Update vignette adjustment
     */
    fun updateVignette(value: Float) {
        _adjustments.value = _adjustments.value.copy(vignette = value)
        applyAdjustments()
    }

    /**
     * Apply preset to image
     */
    fun applyPreset(preset: EditPreset) {
        viewModelScope.launch {
            _uiState.value = EditUiState.Applying
            
            try {
                val imageUri = originalImageUri ?: throw IllegalStateException("No image loaded")
                val editedUri = applyEditPresetUseCase(imageUri, preset)
                
                _adjustments.value = preset.adjustments
                _uiState.value = EditUiState.Success(
                    originalUri = imageUri,
                    editedUri = editedUri
                )
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error(
                    message = e.message ?: "Failed to apply preset"
                )
            }
        }
    }

    /**
     * Apply current adjustments to image
     */
    private fun applyAdjustments() {
        viewModelScope.launch {
            try {
                val imageUri = originalImageUri ?: return@launch
                
                // Create preset from current adjustments
                val customPreset = EditPreset(
                    id = "custom",
                    name = "Custom",
                    description = "Current adjustments",
                    adjustments = _adjustments.value,
                    thumbnail = null
                )
                
                val editedUri = applyEditPresetUseCase(imageUri, customPreset)
                
                _uiState.value = EditUiState.Success(
                    originalUri = imageUri,
                    editedUri = editedUri
                )
            } catch (e: Exception) {
                _uiState.value = EditUiState.Error(
                    message = e.message ?: "Failed to apply adjustments"
                )
            }
        }
    }

    /**
     * Reset all adjustments to default
     */
    fun resetAdjustments() {
        _adjustments.value = ImageAdjustments.default()
        originalImageUri?.let { uri ->
            _uiState.value = EditUiState.Ready(uri)
        }
    }

    /**
     * Save current adjustments as custom preset
     */
    fun saveAsCustomPreset(name: String, description: String) {
        viewModelScope.launch {
            try {
                val preset = EditPreset(
                    id = "custom_${System.currentTimeMillis()}",
                    name = name,
                    description = description,
                    adjustments = _adjustments.value,
                    thumbnail = null // TODO: Generate thumbnail
                )
                
                saveCustomPresetUseCase(preset)
                
                // Reload presets
                loadPresets()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    /**
     * Export edited image
     */
    fun exportImage(): Uri? {
        val currentState = _uiState.value
        return if (currentState is EditUiState.Success) {
            currentState.editedUri
        } else {
            null
        }
    }
}

/**
 * UI State for Edit Screen
 */
sealed class EditUiState {
    object Idle : EditUiState()
    
    data class Ready(
        val imageUri: Uri
    ) : EditUiState()
    
    object Applying : EditUiState()
    
    data class Success(
        val originalUri: Uri,
        val editedUri: Uri
    ) : EditUiState()
    
    data class Error(
        val message: String
    ) : EditUiState()
}
