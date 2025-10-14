package com.example.poselens.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.model.SceneAnalysisResult
import com.example.poselens.domain.repository.ImageRepository
import com.example.poselens.domain.repository.PoseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Combined result containing both scene and pose analysis
 */
data class ComprehensiveAnalysisResult(
    val sceneAnalysis: SceneAnalysisResult? = null,
    val poseAnalysis: PoseResult? = null,
    val suggestions: List<String> = emptyList(),
    val overallScore: Float = 0f
)

/**
 * Use case for comprehensive image analysis combining scene and pose detection
 */
class AnalyzeImageComprehensiveUseCase @Inject constructor(
    private val imageRepository: ImageRepository,
    private val poseRepository: PoseRepository,
    private val getPoseSuggestionsUseCase: GetPoseSuggestionsUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Perform comprehensive analysis on an image
     * @param imageUri URI of the image to analyze
     * @return Flow emitting Result with ComprehensiveAnalysisResult
     */
    suspend operator fun invoke(imageUri: Uri): Flow<Result<ComprehensiveAnalysisResult>> {
        return combine(
            imageRepository.analyzeImage(imageUri),
            poseRepository.detectPose(imageUri)
        ) { sceneResult, poseResult ->
            combineResults(sceneResult, poseResult)
        }.flowOn(dispatcher)
    }
    
    /**
     * Analyze bitmap directly
     * @param bitmap Bitmap to analyze
     * @return Result with ComprehensiveAnalysisResult
     */
    suspend fun analyze(bitmap: Bitmap): Result<ComprehensiveAnalysisResult> = coroutineScope {
        try {
            // Run scene and pose analysis in parallel
            val sceneDeferred = async { imageRepository.analyzeBitmap(bitmap) }
            val poseDeferred = async { poseRepository.detectPoseBitmap(bitmap) }
            
            val sceneResult = sceneDeferred.await()
            val poseResult = poseDeferred.await()
            
            combineResults(sceneResult, poseResult)
        } catch (e: Exception) {
            Result.Error(e, "Failed to analyze image: ${e.message}")
        }
    }
    
    /**
     * Combine scene and pose analysis results
     */
    private suspend fun combineResults(
        sceneResult: Result<SceneAnalysisResult>,
        poseResult: Result<PoseResult>
    ): Result<ComprehensiveAnalysisResult> {
        return when {
            sceneResult is Result.Success && poseResult is Result.Success -> {
                // Get suggestions based on both results
                val suggestions = getPoseSuggestionsUseCase(
                    poseResult.data,
                    sceneResult.data.sceneType.name
                )
                
                val suggestionsList = when (suggestions) {
                    is Result.Success -> suggestions.data
                    else -> emptyList()
                }
                
                // Calculate overall score based on confidence values
                val overallScore = (sceneResult.data.confidence + poseResult.data.confidence) / 2f
                
                Result.Success(
                    ComprehensiveAnalysisResult(
                        sceneAnalysis = sceneResult.data,
                        poseAnalysis = poseResult.data,
                        suggestions = suggestionsList,
                        overallScore = overallScore
                    )
                )
            }
            sceneResult is Result.Success && poseResult is Result.Error -> {
                // Scene detected but no pose
                Result.Success(
                    ComprehensiveAnalysisResult(
                        sceneAnalysis = sceneResult.data,
                        poseAnalysis = null,
                        suggestions = listOf("No person detected. Try including a person in the frame."),
                        overallScore = sceneResult.data.confidence / 2f
                    )
                )
            }
            sceneResult is Result.Error && poseResult is Result.Success -> {
                // Pose detected but scene unclear
                Result.Success(
                    ComprehensiveAnalysisResult(
                        sceneAnalysis = null,
                        poseAnalysis = poseResult.data,
                        suggestions = listOf("Scene unclear. Try adjusting lighting or camera position."),
                        overallScore = poseResult.data.confidence / 2f
                    )
                )
            }
            sceneResult is Result.Loading || poseResult is Result.Loading -> {
                Result.Loading
            }
            else -> {
                val errorMessage = when {
                    sceneResult is Result.Error -> sceneResult.message
                    poseResult is Result.Error -> poseResult.message
                    else -> "Analysis failed"
                }
                Result.Error(
                    Exception("Analysis failed"),
                    errorMessage ?: "Failed to analyze image"
                )
            }
        }
    }
}
