package com.example.poselens.domain.model

import android.graphics.PointF
import android.graphics.RectF

/**
 * Pose Detection Result
 */
data class PoseResult(
    val landmarks: List<PoseLandmark>,
    val confidence: Float,
    val boundingBox: RectF?,
    val matchedTemplate: PoseTemplate?,
    val matchScore: Float = 0f, // 0-100%
    val suggestions: List<String> = emptyList()
)

/**
 * Pose Landmark (Keypoint)
 */
data class PoseLandmark(
    val type: LandmarkType,
    val position: PointF,
    val confidence: Float,
    val inFrameLikelihood: Float = 1.0f
)

/**
 * Landmark Types (33 keypoints from ML Kit)
 */
enum class LandmarkType {
    NOSE,
    LEFT_EYE_INNER, LEFT_EYE, LEFT_EYE_OUTER,
    RIGHT_EYE_INNER, RIGHT_EYE, RIGHT_EYE_OUTER,
    LEFT_EAR, RIGHT_EAR,
    MOUTH_LEFT, MOUTH_RIGHT,
    LEFT_SHOULDER, RIGHT_SHOULDER,
    LEFT_ELBOW, RIGHT_ELBOW,
    LEFT_WRIST, RIGHT_WRIST,
    LEFT_PINKY, RIGHT_PINKY,
    LEFT_INDEX, RIGHT_INDEX,
    LEFT_THUMB, RIGHT_THUMB,
    LEFT_HIP, RIGHT_HIP,
    LEFT_KNEE, RIGHT_KNEE,
    LEFT_ANKLE, RIGHT_ANKLE,
    LEFT_HEEL, RIGHT_HEEL,
    LEFT_FOOT_INDEX, RIGHT_FOOT_INDEX;
    
    companion object {
        fun fromIndex(index: Int): LandmarkType? {
            return values().getOrNull(index)
        }
    }
}

/**
 * Pose Template
 */
data class PoseTemplate(
    val id: String,
    val name: String,
    val category: PoseCategory,
    val description: String,
    val difficulty: Difficulty,
    val thumbnailUrl: String,
    val overlayData: List<PoseLandmark>,
    val instructions: List<String>,
    val tags: List<String>,
    val popularity: Int = 0,
    val isFavorite: Boolean = false
)

/**
 * Pose Category
 */
enum class PoseCategory(val displayName: String) {
    CASUAL("Casual"),
    FASHION("Fashion"),
    PORTRAIT("Portrait"),
    STREET("Street"),
    TRAVEL("Travel"),
    CREATIVE("Creative"),
    SPORT("Sport"),
    COUPLE("Couple"),
    GROUP("Group");
    
    companion object {
        fun fromString(value: String): PoseCategory {
            return values().find { 
                it.name.equals(value, ignoreCase = true) 
            } ?: CASUAL
        }
    }
}

/**
 * Difficulty Level
 */
enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");
    
    companion object {
        fun fromString(value: String): Difficulty {
            return values().find { 
                it.name.equals(value, ignoreCase = true) 
            } ?: EASY
        }
    }
}
