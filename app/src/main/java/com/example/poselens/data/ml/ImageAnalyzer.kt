package com.example.poselens.data.ml

import android.graphics.Bitmap
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.model.SceneAnalysisResult
import com.example.poselens.utils.Constants
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabel
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * ML Kit image analyzer for scene classification
 */
@Singleton
class ImageAnalyzer @Inject constructor(
    private val imageLabeler: ImageLabeler
) {
    
    /**
     * Analyze scene from bitmap
     */
    suspend fun analyzeScene(bitmap: Bitmap): Result<SceneAnalysisResult> {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            
            val labels = suspendCancellableCoroutine { continuation ->
                imageLabeler.process(inputImage)
                    .addOnSuccessListener { detectedLabels ->
                        continuation.resume(detectedLabels)
                    }
                    .addOnFailureListener { e ->
                        continuation.cancel(e)
                    }
            }
            
            val sceneResult = analyzeSceneFromLabels(labels, bitmap)
            
            if (sceneResult.confidence >= Constants.MIN_SCENE_CONFIDENCE) {
                Result.Success(sceneResult)
            } else {
                Result.Error(
                    Exception("Low confidence scene detection"),
                    "Unable to determine scene type clearly"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Failed to analyze scene: ${e.message}")
        }
    }
    
    /**
     * Determine scene type from image labels
     */
    private fun analyzeSceneFromLabels(
        labels: List<ImageLabel>,
        bitmap: Bitmap
    ): SceneAnalysisResult {
        val labelMap = labels.associate { it.text.lowercase() to it.confidence }
        
        // Determine scene type based on labels
        val sceneType = determineSceneType(labelMap)
        val sceneConfidence = calculateSceneConfidence(labelMap, sceneType)
        
        // Determine lighting condition
        val lighting = determineLightingCondition(bitmap)
        
        // Analyze composition
        val composition = analyzeComposition(bitmap)
        
        return SceneAnalysisResult(
            sceneType = sceneType,
            confidence = sceneConfidence,
            lightingCondition = lighting.first,
            lightingConfidence = lighting.second,
            composition = composition,
            detectedObjects = labels.take(5).map { it.text },
            timestamp = System.currentTimeMillis()
        )
    }
    
    /**
     * Determine scene type from labels
     */
    private fun determineSceneType(labelMap: Map<String, Float>): SceneAnalysisResult.SceneType {
        // Nature scene indicators
        val natureKeywords = listOf("tree", "plant", "flower", "grass", "mountain", "forest", "garden", "nature", "outdoor")
        val natureScore = natureKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Beach scene indicators
        val beachKeywords = listOf("beach", "sand", "ocean", "sea", "water", "shore", "coast")
        val beachScore = beachKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Mountain scene indicators
        val mountainKeywords = listOf("mountain", "hill", "peak", "cliff", "rock", "landscape")
        val mountainScore = mountainKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Urban scene indicators
        val urbanKeywords = listOf("building", "street", "city", "urban", "architecture", "road", "car")
        val urbanScore = urbanKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Indoor scene indicators
        val indoorKeywords = listOf("indoor", "room", "wall", "furniture", "table", "chair", "interior")
        val indoorScore = indoorKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Portrait scene indicators
        val portraitKeywords = listOf("person", "face", "portrait", "people", "human")
        val portraitScore = portraitKeywords.sumOf { keyword ->
            labelMap.entries.filter { it.key.contains(keyword) }.sumOf { it.value.toDouble() }
        }.toFloat()
        
        // Find the highest scoring scene type
        val scores = mapOf(
            SceneAnalysisResult.SceneType.NATURE to natureScore,
            SceneAnalysisResult.SceneType.BEACH to beachScore,
            SceneAnalysisResult.SceneType.MOUNTAIN to mountainScore,
            SceneAnalysisResult.SceneType.URBAN to urbanScore,
            SceneAnalysisResult.SceneType.INDOOR to indoorScore,
            SceneAnalysisResult.SceneType.PORTRAIT to portraitScore
        )
        
        return scores.maxByOrNull { it.value }?.key ?: SceneAnalysisResult.SceneType.GENERAL
    }
    
    /**
     * Calculate confidence for scene type
     */
    private fun calculateSceneConfidence(
        labelMap: Map<String, Float>,
        sceneType: SceneAnalysisResult.SceneType
    ): Float {
        // Get average confidence of top 3 labels
        val topLabels = labelMap.values.sortedDescending().take(3)
        return if (topLabels.isNotEmpty()) {
            topLabels.average().toFloat()
        } else {
            0f
        }
    }
    
    /**
     * Determine lighting condition from bitmap
     */
    private fun determineLightingCondition(bitmap: Bitmap): Pair<SceneAnalysisResult.LightingCondition, Float> {
        // Sample pixels to determine brightness
        val sampleSize = 100
        val step = (bitmap.width * bitmap.height) / sampleSize
        var totalBrightness = 0f
        var pixelCount = 0
        
        for (i in 0 until sampleSize) {
            val x = (i * step) % bitmap.width
            val y = (i * step) / bitmap.width
            
            if (y < bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xff
                val g = (pixel shr 8) and 0xff
                val b = pixel and 0xff
                
                // Calculate perceived brightness using luminosity formula
                val brightness = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
                totalBrightness += brightness
                pixelCount++
            }
        }
        
        val averageBrightness = totalBrightness / pixelCount
        
        // Determine lighting condition based on brightness
        val (condition, confidence) = when {
            averageBrightness < 0.2f -> Pair(SceneAnalysisResult.LightingCondition.LOW_LIGHT, 0.8f)
            averageBrightness < 0.35f -> Pair(SceneAnalysisResult.LightingCondition.INDOOR, 0.75f)
            averageBrightness < 0.5f -> Pair(SceneAnalysisResult.LightingCondition.OVERCAST, 0.7f)
            averageBrightness < 0.65f -> Pair(SceneAnalysisResult.LightingCondition.NATURAL, 0.85f)
            averageBrightness < 0.8f -> Pair(SceneAnalysisResult.LightingCondition.BRIGHT, 0.8f)
            else -> Pair(SceneAnalysisResult.LightingCondition.GOLDEN_HOUR, 0.75f)
        }
        
        return Pair(condition, confidence)
    }
    
    /**
     * Analyze composition using rule of thirds
     */
    private fun analyzeComposition(bitmap: Bitmap): SceneAnalysisResult.CompositionAnalysis {
        // Simplified composition analysis
        // In a real app, this would use more sophisticated algorithms
        
        val width = bitmap.width
        val height = bitmap.height
        
        // Define rule of thirds grid points
        val thirdWidth = width / 3f
        val thirdHeight = height / 3f
        
        val intersectionPoints = listOf(
            Pair(thirdWidth, thirdHeight),
            Pair(thirdWidth * 2, thirdHeight),
            Pair(thirdWidth, thirdHeight * 2),
            Pair(thirdWidth * 2, thirdHeight * 2)
        )
        
        // Calculate interest points based on contrast
        val interestPoint = findInterestPoint(bitmap)
        
        // Calculate how close interest point is to rule of thirds intersections
        val minDistance = intersectionPoints.minOf { point ->
            val dx = (point.first - interestPoint.first) / width
            val dy = (point.second - interestPoint.second) / height
            kotlin.math.sqrt(dx * dx + dy * dy)
        }
        
        // Convert distance to score (closer = higher score)
        val ruleOfThirdsScore = (1 - minDistance.coerceIn(0f, 1f)).coerceIn(0f, 1f)
        
        return SceneAnalysisResult.CompositionAnalysis(
            ruleOfThirdsScore = ruleOfThirdsScore,
            subjectPosition = Pair(
                interestPoint.first / width,
                interestPoint.second / height
            ),
            balanceScore = 0.7f, // Placeholder
            depthScore = 0.6f // Placeholder
        )
    }
    
    /**
     * Find point of interest in image (simplified)
     */
    private fun findInterestPoint(bitmap: Bitmap): Pair<Float, Float> {
        // Simplified: find brightest region
        // In a real app, this would use edge detection or saliency detection
        
        val gridSize = 5
        val cellWidth = bitmap.width / gridSize
        val cellHeight = bitmap.height / gridSize
        
        var maxBrightness = 0f
        var maxX = bitmap.width / 2f
        var maxY = bitmap.height / 2f
        
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val x = col * cellWidth + cellWidth / 2
                val y = row * cellHeight + cellHeight / 2
                
                if (x < bitmap.width && y < bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)
                    val r = (pixel shr 16) and 0xff
                    val g = (pixel shr 8) and 0xff
                    val b = pixel and 0xff
                    val brightness = (r + g + b) / (3f * 255f)
                    
                    if (brightness > maxBrightness) {
                        maxBrightness = brightness
                        maxX = x.toFloat()
                        maxY = y.toFloat()
                    }
                }
            }
        }
        
        return Pair(maxX, maxY)
    }
}
