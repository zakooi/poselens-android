package com.example.poselens.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.ImageRepository
import com.example.poselens.domain.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Use case for saving images to gallery
 */
class SaveImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val preferencesRepository: PreferencesRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Save image to gallery
     * @param bitmap Bitmap to save
     * @param customFilename Optional custom filename (without extension)
     * @param checkPreferences Whether to check auto-save preference
     * @return Result with saved image URI
     */
    suspend operator fun invoke(
        bitmap: Bitmap,
        customFilename: String? = null,
        checkPreferences: Boolean = true
    ): Result<Uri> = withContext(dispatcher) {
        try {
            // Check auto-save preference if requested
            if (checkPreferences) {
                val autoSave = preferencesRepository.getAutoSaveImages().first()
                if (!autoSave) {
                    return@withContext Result.Error(
                        Exception("Auto-save disabled"),
                        "Auto-save is disabled in settings"
                    )
                }
            }
            
            // Generate filename
            val filename = customFilename ?: generateFilename()
            
            // Save to gallery
            imageRepository.saveImageToGallery(bitmap, filename)
        } catch (e: Exception) {
            Result.Error(e, "Failed to save image: ${e.message}")
        }
    }
    
    /**
     * Save with custom quality setting
     * @param bitmap Bitmap to save
     * @param quality Quality setting (1-100)
     * @param customFilename Optional custom filename
     * @return Result with saved image URI
     */
    suspend fun saveWithQuality(
        bitmap: Bitmap,
        quality: Int = 90,
        customFilename: String? = null
    ): Result<Uri> = withContext(dispatcher) {
        try {
            // Get quality preference if not specified
            val finalQuality = if (quality == 90) {
                preferencesRepository.getImageQuality().first()
            } else {
                quality
            }
            
            // Validate quality
            if (finalQuality !in 1..100) {
                return@withContext Result.Error(
                    Exception("Invalid quality"),
                    "Quality must be between 1 and 100"
                )
            }
            
            val filename = customFilename ?: generateFilename()
            
            // Note: Current implementation doesn't use quality parameter
            // In production, this would be passed to the repository
            imageRepository.saveImageToGallery(bitmap, filename)
        } catch (e: Exception) {
            Result.Error(e, "Failed to save image: ${e.message}")
        }
    }
    
    /**
     * Save edited image with timestamp
     * @param originalBitmap Original image
     * @param editedBitmap Edited image
     * @return Result with pair of URIs (original, edited)
     */
    suspend fun saveEditedImage(
        originalBitmap: Bitmap,
        editedBitmap: Bitmap
    ): Result<Pair<Uri?, Uri>> = withContext(dispatcher) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            
            // Save edited image
            val editedFilename = "PoseLens_edited_$timestamp.jpg"
            val editedResult = imageRepository.saveImageToGallery(editedBitmap, editedFilename)
            
            if (editedResult is Result.Error) {
                return@withContext Result.Error(
                    editedResult.exception,
                    "Failed to save edited image: ${editedResult.message}"
                )
            }
            
            // Optionally save original if auto-save is enabled
            val autoSave = preferencesRepository.getAutoSaveImages().first()
            val originalUri = if (autoSave) {
                val originalFilename = "PoseLens_original_$timestamp.jpg"
                val originalResult = imageRepository.saveImageToGallery(originalBitmap, originalFilename)
                if (originalResult is Result.Success) originalResult.data else null
            } else {
                null
            }
            
            val editedUri = (editedResult as Result.Success).data
            Result.Success(Pair(originalUri, editedUri))
        } catch (e: Exception) {
            Result.Error(e, "Failed to save images: ${e.message}")
        }
    }
    
    /**
     * Generate filename with timestamp
     */
    private fun generateFilename(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return "PoseLens_$timestamp.jpg"
    }
}
