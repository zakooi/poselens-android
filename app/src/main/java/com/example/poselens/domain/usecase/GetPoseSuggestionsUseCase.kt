package com.example.poselens.domain.usecase

import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.PoseResult
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.PoseRepository
import com.example.poselens.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for getting pose improvement suggestions
 */
class GetPoseSuggestionsUseCase @Inject constructor(
    private val poseRepository: PoseRepository,
    private val templateRepository: TemplateRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Get suggestions for improving current pose
     * @param currentPose Current detected pose
     * @param sceneType Type of scene
     * @param includeTemplateSuggestions Whether to include template matching suggestions
     * @return Result with list of suggestions
     */
    suspend operator fun invoke(
        currentPose: PoseResult,
        sceneType: String,
        includeTemplateSuggestions: Boolean = true
    ): Result<List<String>> = withContext(dispatcher) {
        try {
            val suggestions = mutableListOf<String>()
            
            // Get basic pose suggestions from repository
            val basicSuggestions = poseRepository.getPoseSuggestions(currentPose, sceneType)
            if (basicSuggestions is Result.Success) {
                suggestions.addAll(basicSuggestions.data)
            }
            
            // Add template-based suggestions if requested
            if (includeTemplateSuggestions) {
                val templateSuggestions = getTemplateSuggestions(currentPose, sceneType)
                suggestions.addAll(templateSuggestions)
            }
            
            // Add scene-specific suggestions
            val sceneSuggestions = getSceneSpecificSuggestions(sceneType)
            suggestions.addAll(sceneSuggestions)
            
            // Remove duplicates and limit to top 10
            val uniqueSuggestions = suggestions.distinct().take(10)
            
            Result.Success(uniqueSuggestions)
        } catch (e: Exception) {
            Result.Error(e, "Failed to get suggestions: ${e.message}")
        }
    }
    
    /**
     * Get suggestions based on template matching
     */
    private suspend fun getTemplateSuggestions(
        currentPose: PoseResult,
        sceneType: String
    ): List<String> {
        val suggestions = mutableListOf<String>()
        
        // Get recommended templates for this scene
        val templatesResult = poseRepository.getRecommendedTemplates(sceneType)
        if (templatesResult is Result.Success) {
            val templates = templatesResult.data.take(3) // Top 3 templates
            
            templates.forEach { template ->
                val similarityResult = poseRepository.comparePoseWithTemplate(currentPose, template)
                if (similarityResult is Result.Success) {
                    val similarity = similarityResult.data
                    
                    when {
                        similarity < 0.3f -> {
                            suggestions.add("Try the '${template.name}' pose for better composition")
                        }
                        similarity in 0.3f..0.6f -> {
                            suggestions.add("You're close to '${template.name}' pose - adjust slightly")
                        }
                        similarity > 0.8f -> {
                            suggestions.add("Great! Your pose matches '${template.name}' perfectly")
                        }
                    }
                }
            }
        }
        
        return suggestions
    }
    
    /**
     * Get scene-specific suggestions
     */
    private fun getSceneSpecificSuggestions(sceneType: String): List<String> {
        return when (sceneType.uppercase()) {
            "NATURE" -> listOf(
                "Use natural elements to frame your pose",
                "Consider the rule of thirds for better composition",
                "Natural lighting works best - face the light source"
            )
            "BEACH" -> listOf(
                "Avoid harsh midday sun - golden hour is best",
                "Use the horizon line for balance",
                "Walking poses work well on beaches"
            )
            "MOUNTAIN" -> listOf(
                "Show scale with expansive arm gestures",
                "Use leading lines from trails or ridges",
                "Victory poses work great with mountain views"
            )
            "URBAN" -> listOf(
                "Use architectural lines for composition",
                "Try dynamic walking or jumping poses",
                "Look for interesting backgrounds like walls or buildings"
            )
            "INDOOR" -> listOf(
                "Ensure adequate lighting from windows or lamps",
                "Use furniture or walls for casual leaning poses",
                "Watch for cluttered backgrounds"
            )
            "PORTRAIT" -> listOf(
                "Focus on upper body and facial expression",
                "Angle your body slightly for more flattering lines",
                "Keep chin slightly down for better angles"
            )
            else -> listOf(
                "Maintain good posture",
                "Be natural and relaxed",
                "Experiment with different angles"
            )
        }
    }
}
