package com.example.poselens.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.model.SceneAnalysisResult
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for image-related operations
 */
interface ImageRepository {
    
    /**
     * Analyze an image to detect scene type, lighting, and composition
     * @param imageUri URI of the image to analyze
     * @return Flow emitting Result with SceneAnalysisResult
     */
    suspend fun analyzeImage(imageUri: Uri): Flow<Result<SceneAnalysisResult>>
    
    /**
     * Analyze bitmap directly
     * @param bitmap Bitmap to analyze
     * @return Result with SceneAnalysisResult
     */
    suspend fun analyzeBitmap(bitmap: Bitmap): Result<SceneAnalysisResult>
    
    /**
     * Save an edited image to gallery
     * @param bitmap Image to save
     * @param filename Name for the saved file
     * @return Result with saved file URI
     */
    suspend fun saveImageToGallery(bitmap: Bitmap, filename: String): Result<Uri>
    
    /**
     * Apply filters and adjustments to an image
     * @param bitmap Original bitmap
     * @param brightness Brightness adjustment (-100 to 100)
     * @param contrast Contrast adjustment (0.5 to 1.5)
     * @param saturation Saturation adjustment (0 to 2)
     * @param exposure Exposure adjustment (-2 to 2)
     * @return Result with adjusted bitmap
     */
    suspend fun applyImageAdjustments(
        bitmap: Bitmap,
        brightness: Float = 0f,
        contrast: Float = 1f,
        saturation: Float = 1f,
        exposure: Float = 0f,
        highlights: Float = 0f,
        shadows: Float = 0f,
        temperature: Float = 0f,
        tint: Float = 0f,
        sharpness: Float = 0f,
        vignette: Float = 0f,
        grain: Float = 0f
    ): Result<Bitmap>
    
    /**
     * Get thumbnail for an image
     * @param imageUri URI of the image
     * @param size Target thumbnail size
     * @return Result with thumbnail bitmap
     */
    suspend fun getThumbnail(imageUri: Uri, size: Int = 200): Result<Bitmap>
    
    /**
     * Upload image to server for advanced analysis
     * @param imageUri URI of image to upload
     * @return Result with analysis ID
     */
    suspend fun uploadImageForAnalysis(imageUri: Uri): Result<String>
}
