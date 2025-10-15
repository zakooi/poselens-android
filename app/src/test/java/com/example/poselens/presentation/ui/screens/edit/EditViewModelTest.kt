package com.example.poselens.presentation.ui.screens.edit

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.poselens.domain.model.EditPreset
import com.example.poselens.domain.model.ImageAdjustments
import com.example.poselens.domain.usecase.ApplyEditPresetUseCase
import com.example.poselens.domain.usecase.GetEditPresetsUseCase
import com.example.poselens.domain.usecase.SaveCustomPresetUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Unit tests for EditViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EditViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var applyEditPresetUseCase: ApplyEditPresetUseCase
    private lateinit var getEditPresetsUseCase: GetEditPresetsUseCase
    private lateinit var saveCustomPresetUseCase: SaveCustomPresetUseCase
    private lateinit var viewModel: EditViewModel

    private val mockImageUri = mockk<Uri>(relaxed = true) {
        every { toString() } returns "content://image.jpg"
    }
    private val mockEditedUri = mockk<Uri>(relaxed = true) {
        every { toString() } returns "content://edited_image.jpg"
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        applyEditPresetUseCase = mockk()
        getEditPresetsUseCase = mockk()
        saveCustomPresetUseCase = mockk()
        
        viewModel = EditViewModel(
            applyEditPresetUseCase,
            getEditPresetsUseCase,
            saveCustomPresetUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initial state should be Idle`() {
        // Given - fresh ViewModel
        
        // When - no action taken
        val state = viewModel.uiState.value
        
        // Then
        assertIs<EditUiState.Idle>(state)
    }

    @Test
    fun `initial adjustments should be default`() = runTest {
        // Given - fresh ViewModel
        
        // When
        val adjustments = viewModel.adjustments.first()
        
        // Then
        assertEquals(ImageAdjustments.default(), adjustments)
    }

    @Test
    fun `initializeWithImage should set Ready state and load presets`() = runTest {
        // Given
        val mockPresets = listOf(createMockPreset("Natural"), createMockPreset("Vivid"))
        coEvery { getEditPresetsUseCase() } returns mockPresets
        
        // When
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertIs<EditUiState.Ready>(state)
        assertEquals(mockImageUri, state.imageUri)
        
        // Verify presets loaded
        val presets = viewModel.presets.value
        assertEquals(2, presets.size)
    }

    @Test
    fun `updateBrightness should update adjustments`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        
        // When
        viewModel.updateBrightness(1.2f)
        advanceUntilIdle()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(1.2f, adjustments.brightness)
    }

    @Test
    fun `updateContrast should update adjustments`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        
        // When
        viewModel.updateContrast(1.3f)
        advanceUntilIdle()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(1.3f, adjustments.contrast)
    }

    @Test
    fun `updateSaturation should update adjustments`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        
        // When
        viewModel.updateSaturation(0.9f)
        advanceUntilIdle()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(0.9f, adjustments.saturation)
    }

    @Test
    fun `updateTemperature should update adjustments`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        
        // When
        viewModel.updateTemperature(0.5f)
        advanceUntilIdle()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(0.5f, adjustments.temperature)
    }

    @Test
    fun `applyPreset should update state to Success`() = runTest {
        // Given
        val preset = createMockPreset("Vivid")
        coEvery { applyEditPresetUseCase(any(), any()) } returns mockEditedUri
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // When
        viewModel.applyPreset(preset)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertIs<EditUiState.Success>(state)
        assertEquals(mockImageUri, state.originalUri)
        assertEquals(mockEditedUri, state.editedUri)
        
        // Verify adjustments updated to preset
        val adjustments = viewModel.adjustments.value
        assertEquals(preset.adjustments, adjustments)
    }

    @Test
    fun `applyPreset should set Applying state during processing`() = runTest {
        // Given
        val preset = createMockPreset("Vivid")
        coEvery { applyEditPresetUseCase(any(), any()) } coAnswers {
            delay(100)
            mockEditedUri
        }
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // When
        viewModel.applyPreset(preset)
        
        // Then - check Applying state before completion
        val state = viewModel.uiState.value
        assertIs<EditUiState.Applying>(state)
    }

    @Test
    fun `applyPreset should emit Error on failure`() = runTest {
        // Given
        val preset = createMockPreset("Vivid")
        val errorMessage = "Failed to apply preset"
        coEvery { applyEditPresetUseCase(any(), any()) } throws Exception(errorMessage)
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // When
        viewModel.applyPreset(preset)
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertIs<EditUiState.Error>(state)
        assertEquals(errorMessage, state.message)
    }

    @Test
    fun `resetAdjustments should reset to default values`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        viewModel.updateBrightness(1.5f)
        viewModel.updateContrast(1.3f)
        advanceUntilIdle()
        
        // Verify adjustments changed
        assert(viewModel.adjustments.value.brightness != 1.0f)
        
        // When
        viewModel.resetAdjustments()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(ImageAdjustments.default(), adjustments)
        
        // State should be Ready again
        val state = viewModel.uiState.value
        assertIs<EditUiState.Ready>(state)
    }

    @Test
    fun `saveAsCustomPreset should save preset and reload list`() = runTest {
        // Given
        val initialPresets = listOf(createMockPreset("Natural"))
        val updatedPresets = listOf(
            createMockPreset("Natural"),
            createMockPreset("Custom")
        )
        coEvery { getEditPresetsUseCase() } returnsMany listOf(initialPresets, updatedPresets)
        coEvery { saveCustomPresetUseCase(any()) } just Runs
        
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // Verify initial preset count
        assertEquals(1, viewModel.presets.value.size)
        
        // When
        viewModel.saveAsCustomPreset("My Preset", "Custom description")
        advanceUntilIdle()
        
        // Then
        coVerify { saveCustomPresetUseCase(any()) }
        coVerify(exactly = 2) { getEditPresetsUseCase() }
        
        // Verify presets reloaded
        assertEquals(2, viewModel.presets.value.size)
    }

    @Test
    fun `exportImage should return edited URI in Success state`() = runTest {
        // Given
        val preset = createMockPreset("Vivid")
        coEvery { applyEditPresetUseCase(any(), any()) } returns mockEditedUri
        viewModel.initializeWithImage(mockImageUri)
        viewModel.applyPreset(preset)
        advanceUntilIdle()
        
        // When
        val exportedUri = viewModel.exportImage()
        
        // Then
        assertNotNull(exportedUri)
        assertEquals(mockEditedUri, exportedUri)
    }

    @Test
    fun `exportImage should return null in non-Success state`() {
        // Given - Idle state
        
        // When
        val exportedUri = viewModel.exportImage()
        
        // Then
        assertNull(exportedUri)
    }

    @Test
    fun `multiple adjustment updates should be applied correctly`() = runTest {
        // Given
        viewModel.initializeWithImage(mockImageUri)
        
        // When - apply multiple adjustments
        viewModel.updateBrightness(1.2f)
        viewModel.updateContrast(1.1f)
        viewModel.updateSaturation(0.9f)
        viewModel.updateTemperature(0.3f)
        advanceUntilIdle()
        
        // Then
        val adjustments = viewModel.adjustments.value
        assertEquals(1.2f, adjustments.brightness)
        assertEquals(1.1f, adjustments.contrast)
        assertEquals(0.9f, adjustments.saturation)
        assertEquals(0.3f, adjustments.temperature)
    }

    @Test
    fun `preset load failure should not block UI`() = runTest {
        // Given
        coEvery { getEditPresetsUseCase() } throws Exception("Failed to load presets")
        
        // When
        viewModel.initializeWithImage(mockImageUri)
        advanceUntilIdle()
        
        // Then - state should still be Ready despite preset load failure
        val state = viewModel.uiState.value
        assertIs<EditUiState.Ready>(state)
        
        // Presets should be empty
        assertEquals(0, viewModel.presets.value.size)
    }

    // Helper function to create mock preset
    private fun createMockPreset(name: String): EditPreset {
        return EditPreset(
            id = name.lowercase(),
            name = name,
            description = "$name preset",
            adjustments = ImageAdjustments(
                brightness = 1.1f,
                contrast = 1.2f,
                saturation = 1.0f,
                sharpness = 1.0f,
                temperature = 0.0f,
                tint = 0.0f,
                exposure = 0.0f,
                highlights = 0.0f,
                shadows = 0.0f,
                vignette = 0.0f
            ),
            thumbnail = null
        )
    }
}
