package com.example.poselens.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.model.SceneAnalysisResult
import com.example.poselens.domain.repository.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Use case for analyzing an image to detect scene type, lighting, and composition
 */
class AnalyzeImageUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Analyze image from URI
     * @param imageUri URI of the image to analyze
     * @return Flow emitting Result with SceneAnalysisResult
     */
    suspend operator fun invoke(imageUri: Uri): Flow<Result<SceneAnalysisResult>> {
        return imageRepository.analyzeImage(imageUri)
            .flowOn(dispatcher)
    }
    
    /**
     * Analyze image from Bitmap
     * @param bitmap Bitmap to analyze
     * @return Result with SceneAnalysisResult
     */
    suspend fun analyze(bitmap: Bitmap): Result<SceneAnalysisResult> {
        return imageRepository.analyzeBitmap(bitmap)
    }
}
