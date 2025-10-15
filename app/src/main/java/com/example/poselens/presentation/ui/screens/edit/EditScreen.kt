package com.example.poselens.presentation.ui.screens.edit

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.poselens.presentation.ui.components.*

/**
 * Edit Screen
 * 
 * Features:
 * - Image preview with adjustments
 * - Preset selection
 * - Manual adjustment sliders
 * - Before/After comparison
 * - Save & Share actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    imageUri: Uri?,
    onNavigateBack: () -> Unit,
    viewModel: EditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val adjustments by viewModel.adjustments.collectAsState()
    val presets by viewModel.presets.collectAsState()
    
    var showComparison by remember { mutableStateOf(false) }
    var selectedPresetId by remember { mutableStateOf<String?>(null) }
    
    // Initialize with image
    LaunchedEffect(imageUri) {
        imageUri?.let {
            viewModel.initializeWithImage(it)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Image") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Reset button
                    IconButton(onClick = { viewModel.resetAdjustments() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                    
                    // Share button
                    if (uiState is EditUiState.Success) {
                        IconButton(onClick = { /* TODO: Share */ }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                    
                    // Save button
                    if (uiState is EditUiState.Success) {
                        IconButton(onClick = { /* TODO: Save */ }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is EditUiState.Idle -> {
                EmptyState(modifier = Modifier.padding(paddingValues))
            }
            
            is EditUiState.Ready -> {
                EditContent(
                    imageUri = state.imageUri,
                    adjustments = adjustments,
                    presets = presets,
                    selectedPresetId = selectedPresetId,
                    onPresetSelected = { preset ->
                        selectedPresetId = preset.id
                        viewModel.applyPreset(preset)
                    },
                    onBrightnessChange = viewModel::updateBrightness,
                    onContrastChange = viewModel::updateContrast,
                    onSaturationChange = viewModel::updateSaturation,
                    onSharpnessChange = viewModel::updateSharpness,
                    onTemperatureChange = viewModel::updateTemperature,
                    onTintChange = viewModel::updateTint,
                    onExposureChange = viewModel::updateExposure,
                    onHighlightsChange = viewModel::updateHighlights,
                    onShadowsChange = viewModel::updateShadows,
                    onVignetteChange = viewModel::updateVignette,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            is EditUiState.Applying -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }
            
            is EditUiState.Success -> {
                if (showComparison) {
                    ComparisonContent(
                        beforeUri = state.originalUri,
                        afterUri = state.editedUri,
                        onToggleComparison = { showComparison = false },
                        modifier = Modifier.padding(paddingValues)
                    )
                } else {
                    EditContent(
                        imageUri = state.editedUri,
                        adjustments = adjustments,
                        presets = presets,
                        selectedPresetId = selectedPresetId,
                        onPresetSelected = { preset ->
                            selectedPresetId = preset.id
                            viewModel.applyPreset(preset)
                        },
                        onBrightnessChange = viewModel::updateBrightness,
                        onContrastChange = viewModel::updateContrast,
                        onSaturationChange = viewModel::updateSaturation,
                        onSharpnessChange = viewModel::updateSharpness,
                        onTemperatureChange = viewModel::updateTemperature,
                        onTintChange = viewModel::updateTint,
                        onExposureChange = viewModel::updateExposure,
                        onHighlightsChange = viewModel::updateHighlights,
                        onShadowsChange = viewModel::updateShadows,
                        onVignetteChange = viewModel::updateVignette,
                        onShowComparison = { showComparison = true },
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            
            is EditUiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun EditContent(
    imageUri: Uri,
    adjustments: com.example.poselens.domain.model.ImageAdjustments,
    presets: List<com.example.poselens.domain.model.EditPreset>,
    selectedPresetId: String?,
    onPresetSelected: (com.example.poselens.domain.model.EditPreset) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onContrastChange: (Float) -> Unit,
    onSaturationChange: (Float) -> Unit,
    onSharpnessChange: (Float) -> Unit,
    onTemperatureChange: (Float) -> Unit,
    onTintChange: (Float) -> Unit,
    onExposureChange: (Float) -> Unit,
    onHighlightsChange: (Float) -> Unit,
    onShadowsChange: (Float) -> Unit,
    onVignetteChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onShowComparison: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Image Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp)
        ) {
            ZoomableImage(
                imageUri = imageUri,
                contentDescription = "Edited image"
            )
        }
        
        // Comparison button
        onShowComparison?.let {
            Button(
                onClick = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Compare Before/After")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Presets
        if (presets.isNotEmpty()) {
            Text(
                text = "Presets",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            PresetListRow(
                presets = presets,
                selectedPresetId = selectedPresetId,
                onPresetClick = onPresetSelected
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Manual Adjustments
        Text(
            text = "Adjustments",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Basic adjustments
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PercentageAdjustmentSlider(
                label = "Brightness",
                value = adjustments.brightness,
                onValueChange = onBrightnessChange
            )
            
            PercentageAdjustmentSlider(
                label = "Contrast",
                value = adjustments.contrast,
                onValueChange = onContrastChange
            )
            
            PercentageAdjustmentSlider(
                label = "Saturation",
                value = adjustments.saturation,
                onValueChange = onSaturationChange
            )
            
            PercentageAdjustmentSlider(
                label = "Sharpness",
                value = adjustments.sharpness,
                onValueChange = onSharpnessChange
            )
            
            OffsetAdjustmentSlider(
                label = "Temperature",
                value = adjustments.temperature,
                onValueChange = onTemperatureChange
            )
            
            OffsetAdjustmentSlider(
                label = "Tint",
                value = adjustments.tint,
                onValueChange = onTintChange
            )
            
            OffsetAdjustmentSlider(
                label = "Exposure",
                value = adjustments.exposure,
                onValueChange = onExposureChange
            )
            
            OffsetAdjustmentSlider(
                label = "Highlights",
                value = adjustments.highlights,
                onValueChange = onHighlightsChange
            )
            
            OffsetAdjustmentSlider(
                label = "Shadows",
                value = adjustments.shadows,
                onValueChange = onShadowsChange
            )
            
            OffsetAdjustmentSlider(
                label = "Vignette",
                value = adjustments.vignette,
                onValueChange = onVignetteChange
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ComparisonContent(
    beforeUri: Uri,
    afterUri: Uri,
    onToggleComparison: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        ComparisonView(
            beforeUri = beforeUri,
            afterUri = afterUri,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        )
        
        Button(
            onClick = onToggleComparison,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Back to Edit")
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text("No image selected")
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
    }
}
