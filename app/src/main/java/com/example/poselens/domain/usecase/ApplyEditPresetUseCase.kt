package com.example.poselens.domain.usecase

import android.graphics.Bitmap
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.EditPreset
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for applying edit presets to images
 */
class ApplyEditPresetUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Apply an edit preset to a bitmap
     * @param bitmap Original bitmap
     * @param preset Edit preset to apply
     * @return Result with edited bitmap
     */
    suspend operator fun invoke(
        bitmap: Bitmap,
        preset: EditPreset
    ): Result<Bitmap> = withContext(dispatcher) {
        try {
            imageRepository.applyImageAdjustments(
                bitmap = bitmap,
                brightness = preset.adjustments.brightness,
                contrast = preset.adjustments.contrast,
                saturation = preset.adjustments.saturation,
                exposure = preset.adjustments.exposure,
                highlights = preset.adjustments.highlights,
                shadows = preset.adjustments.shadows,
                temperature = preset.adjustments.temperature,
                tint = preset.adjustments.tint,
                sharpness = preset.adjustments.sharpness,
                vignette = preset.adjustments.vignette,
                grain = preset.adjustments.grain
            )
        } catch (e: Exception) {
            Result.Error(e, "Failed to apply preset: ${e.message}")
        }
    }
    
    /**
     * Apply custom adjustments to a bitmap
     * @param bitmap Original bitmap
     * @param brightness Brightness adjustment (-100 to 100)
     * @param contrast Contrast adjustment (0.5 to 1.5)
     * @param saturation Saturation adjustment (0 to 2)
     * @return Result with edited bitmap
     */
    suspend fun applyCustomAdjustments(
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
    ): Result<Bitmap> = withContext(dispatcher) {
        imageRepository.applyImageAdjustments(
            bitmap = bitmap,
            brightness = brightness,
            contrast = contrast,
            saturation = saturation,
            exposure = exposure,
            highlights = highlights,
            shadows = shadows,
            temperature = temperature,
            tint = tint,
            sharpness = sharpness,
            vignette = vignette,
            grain = grain
        )
    }
    
    /**
     * Preview a preset on a bitmap (returns a lower resolution version for quick preview)
     * @param bitmap Original bitmap
     * @param preset Preset to preview
     * @return Result with preview bitmap
     */
    suspend fun previewPreset(
        bitmap: Bitmap,
        preset: EditPreset
    ): Result<Bitmap> = withContext(dispatcher) {
        try {
            // Create a smaller version for quick preview
            val previewWidth = 400
            val previewHeight = (bitmap.height * (previewWidth.toFloat() / bitmap.width)).toInt()
            val previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, true)
            
            // Apply preset to preview
            val result = imageRepository.applyImageAdjustments(
                bitmap = previewBitmap,
                brightness = preset.adjustments.brightness,
                contrast = preset.adjustments.contrast,
                saturation = preset.adjustments.saturation,
                exposure = preset.adjustments.exposure,
                highlights = preset.adjustments.highlights,
                shadows = preset.adjustments.shadows,
                temperature = preset.adjustments.temperature,
                tint = preset.adjustments.tint,
                sharpness = preset.adjustments.sharpness,
                vignette = preset.adjustments.vignette,
                grain = preset.adjustments.grain
            )
            
            // Clean up preview bitmap if result is success
            if (result is Result.Success) {
                previewBitmap.recycle()
            }
            
            result
        } catch (e: Exception) {
            Result.Error(e, "Failed to preview preset: ${e.message}")
        }
    }
}
