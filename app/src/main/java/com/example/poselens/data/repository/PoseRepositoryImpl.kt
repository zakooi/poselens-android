package com.example.poselens.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.data.local.dao.PoseHistoryDao
import com.example.poselens.data.local.entity.PoseHistoryEntity
import com.example.poselens.data.ml.PoseEstimator
import com.example.poselens.data.remote.ApiService
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.PoseTemplate
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.PoseRepository
import com.example.poselens.utils.ImageUtils
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PoseRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val poseEstimator: PoseEstimator,
    private val poseHistoryDao: PoseHistoryDao,
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) : PoseRepository {
    
    private val gson = Gson()
    
    override suspend fun detectPose(imageUri: Uri): Flow<Result<PoseResult>> = flow {
        emit(Result.Loading)
        
        try {
            val bitmap = ImageUtils.loadBitmapFromUri(context, imageUri)
                ?: throw Exception("Failed to load image")
            
            val result = poseEstimator.detectPose(bitmap)
            
            // Save to history if successful
            if (result is Result.Success) {
                savePoseHistory(imageUri, result.data)
            }
            
            emit(result)
            bitmap.recycle()
        } catch (e: Exception) {
            emit(Result.Error(e, "Failed to detect pose: ${e.message}"))
        }
    }.flowOn(ioDispatcher)
    
    override suspend fun detectPoseBitmap(bitmap: Bitmap): Result<PoseResult> {
        return withContext(ioDispatcher) {
            poseEstimator.detectPose(bitmap)
        }
    }
    
    override suspend fun getPoseSuggestions(
        currentPose: PoseResult,
        sceneType: String
    ): Result<List<String>> {
        return withContext(ioDispatcher) {
            try {
                // First check local quality issues
                val localSuggestions = poseEstimator.validatePoseQuality(currentPose)
                
                // Then try to get API suggestions
                val poseJson = gson.toJson(currentPose.landmarks)
                val response = apiService.getPoseSuggestions(sceneType, poseJson)
                
                val allSuggestions = if (response.isSuccessful && response.body()?.success == true) {
                    localSuggestions + (response.body()?.data ?: emptyList())
                } else {
                    localSuggestions + listOf(
                        "Stand naturally with good posture",
                        "Ensure your full body is visible in the frame",
                        "Try different angles for better composition"
                    )
                }
                
                Result.Success(allSuggestions.distinct())
            } catch (e: Exception) {
                // Fallback to local suggestions only
                val suggestions = poseEstimator.validatePoseQuality(currentPose)
                Result.Success(suggestions.ifEmpty {
                    listOf("Great pose! Try experimenting with different angles.")
                })
            }
        }
    }
    
    override suspend fun comparePoseWithTemplate(
        currentPose: PoseResult,
        template: PoseTemplate
    ): Result<Float> {
        return withContext(ioDispatcher) {
            try {
                val templatePose = PoseResult(
                    landmarks = template.landmarkPositions,
                    confidence = template.confidence,
                    detectedAt = System.currentTimeMillis()
                )
                
                val similarity = poseEstimator.calculatePoseSimilarity(currentPose, templatePose)
                Result.Success(similarity)
            } catch (e: Exception) {
                Result.Error(e, "Failed to compare poses: ${e.message}")
            }
        }
    }
    
    override suspend fun getRecommendedTemplates(
        sceneType: String,
        category: String?
    ): Result<List<PoseTemplate>> {
        return withContext(ioDispatcher) {
            try {
                // This would fetch from TemplateRepository
                // For now, return empty list as placeholder
                Result.Success(emptyList())
            } catch (e: Exception) {
                Result.Error(e, "Failed to get recommended templates: ${e.message}")
            }
        }
    }
    
    override fun trackPoseRealtime(bitmap: Bitmap): Flow<Result<PoseResult>> = flow {
        val result = poseEstimator.detectPose(bitmap)
        emit(result)
    }.flowOn(ioDispatcher)
    
    override suspend fun savePoseAsTemplate(
        poseResult: PoseResult,
        name: String,
        category: String
    ): Result<Long> {
        return withContext(ioDispatcher) {
            try {
                // This would save to TemplateRepository
                // For now, just return success
                Result.Success(System.currentTimeMillis())
            } catch (e: Exception) {
                Result.Error(e, "Failed to save template: ${e.message}")
            }
        }
    }
    
    private suspend fun savePoseHistory(imageUri: Uri, poseResult: PoseResult) {
        try {
            val entity = PoseHistoryEntity(
                imageUri = imageUri.toString(),
                landmarkPositions = gson.toJson(poseResult.landmarks),
                confidence = poseResult.confidence,
                detectedAt = poseResult.detectedAt
            )
            poseHistoryDao.insertHistory(entity)
        } catch (e: Exception) {
            // Silent fail for history
        }
    }
}
