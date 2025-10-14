package com.example.poselens.domain.usecase

import android.net.Uri
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.ImageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for uploading images for cloud analysis
 */
class UploadImageForAnalysisUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Upload image to server for advanced analysis
     * @param imageUri URI of image to upload
     * @return Result with analysis ID that can be used to retrieve results
     */
    suspend operator fun invoke(imageUri: Uri): Result<String> {
        return withContext(dispatcher) {
            imageRepository.uploadImageForAnalysis(imageUri)
        }
    }
    
    /**
     * Upload multiple images in batch
     * @param imageUris List of image URIs
     * @return Result with list of analysis IDs
     */
    suspend fun uploadBatch(imageUris: List<Uri>): Result<List<String>> {
        return withContext(dispatcher) {
            try {
                val analysisIds = mutableListOf<String>()
                
                imageUris.forEach { uri ->
                    val result = imageRepository.uploadImageForAnalysis(uri)
                    when (result) {
                        is Result.Success -> analysisIds.add(result.data)
                        is Result.Error -> {
                            return@withContext Result.Error(
                                result.exception,
                                "Failed to upload ${uri.lastPathSegment}: ${result.message}"
                            )
                        }
                        is Result.Loading -> {} // Skip loading state
                    }
                }
                
                Result.Success(analysisIds)
            } catch (e: Exception) {
                Result.Error(e, "Batch upload failed: ${e.message}")
            }
        }
    }
}
