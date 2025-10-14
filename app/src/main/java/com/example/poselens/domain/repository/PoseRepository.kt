package com.example.poselens.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.PoseTemplate
import com.example.poselens.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for pose detection and analysis
 */
interface PoseRepository {
    
    /**
     * Detect pose from an image
     * @param imageUri URI of the image to analyze
     * @return Flow emitting Result with PoseResult
     */
    suspend fun detectPose(imageUri: Uri): Flow<Result<PoseResult>>
    
    /**
     * Detect pose from bitmap
     * @param bitmap Bitmap to analyze
     * @return Result with PoseResult
     */
    suspend fun detectPoseBitmap(bitmap: Bitmap): Result<PoseResult>
    
    /**
     * Get pose suggestions based on detected pose and scene
     * @param currentPose Current detected pose
     * @param sceneType Type of scene
     * @return Result with list of suggested improvements
     */
    suspend fun getPoseSuggestions(
        currentPose: PoseResult,
        sceneType: String
    ): Result<List<String>>
    
    /**
     * Compare current pose with a template
     * @param currentPose Current detected pose
     * @param template Template to compare against
     * @return Result with similarity score (0-1)
     */
    suspend fun comparePoseWithTemplate(
        currentPose: PoseResult,
        template: PoseTemplate
    ): Result<Float>
    
    /**
     * Get recommended pose templates for a scene type
     * @param sceneType Type of scene
     * @param category Optional category filter
     * @return Result with list of templates
     */
    suspend fun getRecommendedTemplates(
        sceneType: String,
        category: String? = null
    ): Result<List<PoseTemplate>>
    
    /**
     * Track pose in real-time for camera preview
     * @param bitmap Current camera frame
     * @return Flow emitting pose updates
     */
    fun trackPoseRealtime(bitmap: Bitmap): Flow<Result<PoseResult>>
    
    /**
     * Save a custom pose as template
     * @param poseResult Pose to save
     * @param name Template name
     * @param category Template category
     * @return Result with saved template ID
     */
    suspend fun savePoseAsTemplate(
        poseResult: PoseResult,
        name: String,
        category: String
    ): Result<Long>
}
