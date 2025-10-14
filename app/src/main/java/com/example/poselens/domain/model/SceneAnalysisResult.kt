package com.example.poselens.domain.model

import android.graphics.PointF
import android.graphics.RectF

/**
 * Scene Analysis Result
 * Contains information about the analyzed scene
 */
data class SceneAnalysisResult(
    val sceneType: SceneType,
    val confidence: Float, // 0.0 - 1.0
    val lighting: LightingCondition,
    val composition: CompositionAnalysis,
    val suggestions: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Scene Type enumeration
 */
enum class SceneType(val displayName: String) {
    PORTRAIT_SINGLE("Portrait - Single Person"),
    PORTRAIT_GROUP("Portrait - Group"),
    PORTRAIT_SELFIE("Selfie"),
    LANDSCAPE_SUNSET("Landscape - Sunset"),
    LANDSCAPE_NATURE("Landscape - Nature"),
    LANDSCAPE_URBAN("Landscape - Urban"),
    FOOD_OVERHEAD("Food - Overhead"),
    FOOD_45_DEGREE("Food - 45°"),
    FOOD_CLOSEUP("Food - Close-up"),
    PRODUCT_FLATLAY("Product - Flat Lay"),
    PRODUCT_LIFESTYLE("Product - Lifestyle"),
    STREET_PHOTOGRAPHY("Street Photography"),
    OTHER("Other");
    
    companion object {
        fun fromLabel(label: String): SceneType {
            return values().find { 
                it.displayName.contains(label, ignoreCase = true) 
            } ?: OTHER
        }
    }
}

/**
 * Lighting Condition
 */
data class LightingCondition(
    val type: LightingType,
    val quality: LightingQuality,
    val direction: LightDirection,
    val intensity: Float // 0.0 - 1.0
)

enum class LightingType {
    NATURAL, ARTIFICIAL, MIXED, LOW_LIGHT
}

enum class LightingQuality {
    EXCELLENT, GOOD, FAIR, POOR
}

enum class LightDirection {
    FRONT, BACK, SIDE_LEFT, SIDE_RIGHT, TOP, BOTTOM, UNKNOWN
}

/**
 * Composition Analysis
 */
data class CompositionAnalysis(
    val ruleOfThirds: RuleOfThirdsAnalysis,
    val balance: BalanceQuality,
    val spatialDistribution: SpatialDistribution
)

data class RuleOfThirdsAnalysis(
    val applied: Boolean,
    val suggestion: String
)

enum class BalanceQuality {
    EXCELLENT, GOOD, FAIR, POOR
}

enum class SpatialDistribution {
    CENTERED, LEFT_ALIGNED, RIGHT_ALIGNED, 
    TOP_ALIGNED, BOTTOM_ALIGNED, SCATTERED
}
