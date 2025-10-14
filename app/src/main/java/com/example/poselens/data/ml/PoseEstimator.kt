package com.example.poselens.data.ml

import android.graphics.Bitmap
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.Result
import com.example.poselens.utils.Constants
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * ML Kit pose estimation analyzer
 */
@Singleton
class PoseEstimator @Inject constructor(
    private val poseDetector: PoseDetector
) {
    
    /**
     * Detect pose from bitmap using ML Kit
     */
    suspend fun detectPose(bitmap: Bitmap): Result<PoseResult> {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            
            val pose = suspendCancellableCoroutine { continuation ->
                poseDetector.process(inputImage)
                    .addOnSuccessListener { detectedPose ->
                        continuation.resume(detectedPose)
                    }
                    .addOnFailureListener { e ->
                        continuation.cancel(e)
                    }
            }
            
            val poseResult = convertPoseToPoseResult(pose, bitmap.width, bitmap.height)
            
            if (poseResult.confidence >= Constants.MIN_POSE_CONFIDENCE) {
                Result.Success(poseResult)
            } else {
                Result.Error(
                    Exception("Low confidence pose detection"),
                    "No clear pose detected. Please ensure the person is fully visible."
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to detect pose: ${e.message}")
        }
    }
    
    /**
     * Convert ML Kit Pose to domain PoseResult
     */
    private fun convertPoseToPoseResult(
        pose: Pose,
        imageWidth: Int,
        imageHeight: Int
    ): PoseResult {
        val landmarks = mutableMapOf<String, Pair<Float, Float>>()
        var totalConfidence = 0f
        var landmarkCount = 0
        
        // Extract all landmarks
        PoseResult.LandmarkType.values().forEach { landmarkType ->
            val mlKitLandmark = getMlKitLandmark(pose, landmarkType)
            if (mlKitLandmark != null) {
                // Normalize coordinates to 0-1 range
                val normalizedX = mlKitLandmark.position.x / imageWidth
                val normalizedY = mlKitLandmark.position.y / imageHeight
                
                landmarks[landmarkType.name] = Pair(normalizedX, normalizedY)
                
                totalConfidence += mlKitLandmark.inFrameLikelihood
                landmarkCount++
            }
        }
        
        val averageConfidence = if (landmarkCount > 0) {
            totalConfidence / landmarkCount
        } else {
            0f
        }
        
        return PoseResult(
            landmarks = landmarks,
            confidence = averageConfidence,
            detectedAt = System.currentTimeMillis(),
            imageWidth = imageWidth,
            imageHeight = imageHeight
        )
    }
    
    /**
     * Get ML Kit landmark for a specific landmark type
     */
    private fun getMlKitLandmark(pose: Pose, landmarkType: PoseResult.LandmarkType): PoseLandmark? {
        return when (landmarkType) {
            PoseResult.LandmarkType.NOSE -> pose.getPoseLandmark(PoseLandmark.NOSE)
            PoseResult.LandmarkType.LEFT_EYE_INNER -> pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
            PoseResult.LandmarkType.LEFT_EYE -> pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
            PoseResult.LandmarkType.LEFT_EYE_OUTER -> pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
            PoseResult.LandmarkType.RIGHT_EYE_INNER -> pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
            PoseResult.LandmarkType.RIGHT_EYE -> pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
            PoseResult.LandmarkType.RIGHT_EYE_OUTER -> pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
            PoseResult.LandmarkType.LEFT_EAR -> pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
            PoseResult.LandmarkType.RIGHT_EAR -> pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
            PoseResult.LandmarkType.MOUTH_LEFT -> pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
            PoseResult.LandmarkType.MOUTH_RIGHT -> pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)
            PoseResult.LandmarkType.LEFT_SHOULDER -> pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
            PoseResult.LandmarkType.RIGHT_SHOULDER -> pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
            PoseResult.LandmarkType.LEFT_ELBOW -> pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
            PoseResult.LandmarkType.RIGHT_ELBOW -> pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
            PoseResult.LandmarkType.LEFT_WRIST -> pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
            PoseResult.LandmarkType.RIGHT_WRIST -> pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
            PoseResult.LandmarkType.LEFT_PINKY -> pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
            PoseResult.LandmarkType.RIGHT_PINKY -> pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
            PoseResult.LandmarkType.LEFT_INDEX -> pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
            PoseResult.LandmarkType.RIGHT_INDEX -> pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
            PoseResult.LandmarkType.LEFT_THUMB -> pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
            PoseResult.LandmarkType.RIGHT_THUMB -> pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
            PoseResult.LandmarkType.LEFT_HIP -> pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
            PoseResult.LandmarkType.RIGHT_HIP -> pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
            PoseResult.LandmarkType.LEFT_KNEE -> pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
            PoseResult.LandmarkType.RIGHT_KNEE -> pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
            PoseResult.LandmarkType.LEFT_ANKLE -> pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
            PoseResult.LandmarkType.RIGHT_ANKLE -> pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
            PoseResult.LandmarkType.LEFT_HEEL -> pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
            PoseResult.LandmarkType.RIGHT_HEEL -> pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
            PoseResult.LandmarkType.LEFT_FOOT_INDEX -> pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
            PoseResult.LandmarkType.RIGHT_FOOT_INDEX -> pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
        }
    }
    
    /**
     * Calculate similarity between two poses (0-1 score)
     */
    fun calculatePoseSimilarity(pose1: PoseResult, pose2: PoseResult): Float {
        val commonLandmarks = pose1.landmarks.keys.intersect(pose2.landmarks.keys)
        
        if (commonLandmarks.isEmpty()) return 0f
        
        var totalDistance = 0f
        commonLandmarks.forEach { landmarkKey ->
            val point1 = pose1.landmarks[landmarkKey]!!
            val point2 = pose2.landmarks[landmarkKey]!!
            
            val distance = calculateEuclideanDistance(point1, point2)
            totalDistance += distance
        }
        
        val averageDistance = totalDistance / commonLandmarks.size
        
        // Convert distance to similarity score (closer = higher score)
        // Using exponential decay: e^(-k*distance)
        val k = 5f // Sensitivity parameter
        val similarity = kotlin.math.exp(-k * averageDistance)
        
        return similarity.coerceIn(0f, 1f)
    }
    
    /**
     * Calculate Euclidean distance between two points
     */
    private fun calculateEuclideanDistance(
        point1: Pair<Float, Float>,
        point2: Pair<Float, Float>
    ): Float {
        val dx = point1.first - point2.first
        val dy = point1.second - point2.second
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
    
    /**
     * Validate pose quality
     */
    fun validatePoseQuality(poseResult: PoseResult): List<String> {
        val issues = mutableListOf<String>()
        
        // Check if key body parts are visible
        val requiredLandmarks = listOf(
            PoseResult.LandmarkType.LEFT_SHOULDER,
            PoseResult.LandmarkType.RIGHT_SHOULDER,
            PoseResult.LandmarkType.LEFT_HIP,
            PoseResult.LandmarkType.RIGHT_HIP
        )
        
        val missingLandmarks = requiredLandmarks.filter { 
            !poseResult.landmarks.containsKey(it.name) 
        }
        
        if (missingLandmarks.isNotEmpty()) {
            issues.add("Some body parts are not visible. Please ensure full body is in frame.")
        }
        
        // Check pose symmetry
        if (!isPoseSymmetrical(poseResult)) {
            issues.add("Pose appears asymmetrical. Try to center yourself in the frame.")
        }
        
        // Check if pose is too small
        if (isPoseTooSmall(poseResult)) {
            issues.add("You appear too far from camera. Move closer for better detection.")
        }
        
        return issues
    }
    
    /**
     * Check if pose is symmetrical
     */
    private fun isPoseSymmetrical(poseResult: PoseResult): Boolean {
        val leftShoulder = poseResult.landmarks[PoseResult.LandmarkType.LEFT_SHOULDER.name]
        val rightShoulder = poseResult.landmarks[PoseResult.LandmarkType.RIGHT_SHOULDER.name]
        
        if (leftShoulder == null || rightShoulder == null) return true
        
        val shoulderYDiff = kotlin.math.abs(leftShoulder.second - rightShoulder.second)
        return shoulderYDiff < 0.1f // Threshold for symmetry
    }
    
    /**
     * Check if pose is too small in frame
     */
    private fun isPoseTooSmall(poseResult: PoseResult): Boolean {
        val landmarks = poseResult.landmarks.values
        if (landmarks.isEmpty()) return false
        
        val minX = landmarks.minOf { it.first }
        val maxX = landmarks.maxOf { it.first }
        val minY = landmarks.minOf { it.second }
        val maxY = landmarks.maxOf { it.second }
        
        val poseWidth = maxX - minX
        val poseHeight = maxY - minY
        
        // Pose should occupy at least 30% of frame width or height
        return poseWidth < 0.3f && poseHeight < 0.3f
    }
}
