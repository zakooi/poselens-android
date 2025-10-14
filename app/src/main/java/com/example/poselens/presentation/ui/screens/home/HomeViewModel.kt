package com.example.poselens.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    // TODO: Inject repositories when ready
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CameraClicked -> handleCameraClick()
            is HomeEvent.GalleryClicked -> handleGalleryClick()
            is HomeEvent.TemplateClicked -> handleTemplateClick(event.templateId)
        }
    }
    
    private fun handleCameraClick() {
        // TODO: Navigate to camera
    }
    
    private fun handleGalleryClick() {
        // TODO: Open gallery picker
    }
    
    private fun handleTemplateClick(templateId: String) {
        // TODO: Navigate to template detail
    }
}

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val recentPhotos: List<String> = emptyList(),
    val featuredTemplates: List<String> = emptyList()
)

/**
 * Events for Home Screen
 */
sealed class HomeEvent {
    object CameraClicked : HomeEvent()
    object GalleryClicked : HomeEvent()
    data class TemplateClicked(val templateId: String) : HomeEvent()
}
