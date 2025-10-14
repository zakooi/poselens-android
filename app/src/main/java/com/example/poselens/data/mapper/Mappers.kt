package com.example.poselens.data.mapper

import com.example.poselens.data.local.entity.PoseTemplateEntity
import com.example.poselens.data.remote.dto.*
import com.example.poselens.domain.model.*
import com.google.gson.Gson

/**
 * Mappers to convert between different layers
 */

private val gson = Gson()

/**
 * Convert PoseTemplateDto to domain PoseTemplate
 */
fun PoseTemplateDto.toDomain(): PoseTemplate {
    val landmarkMap = landmarks.mapValues { (_, landmark) ->
        Pair(landmark.x, landmark.y)
    }
    
    return PoseTemplate(
        id = id,
        name = name,
        description = description,
        category = PoseResult.PoseCategory.valueOf(category.uppercase()),
        difficulty = PoseTemplate.Difficulty.valueOf(difficulty.uppercase()),
        thumbnailUrl = thumbnailUrl,
        imageUrl = imageUrl,
        landmarkPositions = landmarkMap,
        confidence = 1.0f,
        tags = tags
    )
}

/**
 * Convert domain PoseTemplate to PoseTemplateEntity
 */
fun PoseTemplate.toEntity(
    isFavorite: Boolean = false,
    usageCount: Int = 0,
    lastUsedTimestamp: Long = 0L,
    isCustom: Boolean = false
): PoseTemplateEntity {
    return PoseTemplateEntity(
        id = id,
        name = name,
        description = description,
        category = category.name,
        difficulty = difficulty.name,
        thumbnailUrl = thumbnailUrl,
        imageUrl = imageUrl,
        landmarkPositions = gson.toJson(landmarkPositions),
        confidence = confidence,
        tags = gson.toJson(tags),
        isFavorite = isFavorite,
        usageCount = usageCount,
        lastUsedTimestamp = lastUsedTimestamp,
        isCustom = isCustom
    )
}

/**
 * Convert PoseTemplateEntity to domain PoseTemplate
 */
fun PoseTemplateEntity.toDomain(): PoseTemplate {
    val landmarkMap = gson.fromJson(
        landmarkPositions,
        object : com.google.gson.reflect.TypeToken<Map<String, Pair<Float, Float>>>() {}.type
    ) ?: emptyMap<String, Pair<Float, Float>>()
    
    val tagList = gson.fromJson(
        tags,
        object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
    ) ?: emptyList<String>()
    
    return PoseTemplate(
        id = id,
        name = name,
        description = description,
        category = runCatching { PoseResult.PoseCategory.valueOf(category.uppercase()) }
            .getOrDefault(PoseResult.PoseCategory.GENERAL),
        difficulty = runCatching { PoseTemplate.Difficulty.valueOf(difficulty.uppercase()) }
            .getOrDefault(PoseTemplate.Difficulty.MEDIUM),
        thumbnailUrl = thumbnailUrl,
        imageUrl = imageUrl,
        landmarkPositions = landmarkMap,
        confidence = confidence,
        tags = tagList
    )
}

/**
 * Convert SceneAnalysisDto to domain SceneAnalysisResult
 */
fun SceneAnalysisDto.toDomain(): SceneAnalysisResult {
    val sceneTypeEnum = runCatching {
        SceneAnalysisResult.SceneType.valueOf(sceneType.uppercase().replace(" ", "_"))
    }.getOrDefault(SceneAnalysisResult.SceneType.GENERAL)
    
    val lightingEnum = runCatching {
        SceneAnalysisResult.LightingCondition.valueOf(lightingCondition.uppercase().replace(" ", "_"))
    }.getOrDefault(SceneAnalysisResult.LightingCondition.NATURAL)
    
    val compositionAnalysis = composition?.let {
        SceneAnalysisResult.CompositionAnalysis(
            ruleOfThirdsScore = it.ruleOfThirdsScore,
            subjectPosition = Pair(it.subjectPosition.x, it.subjectPosition.y),
            balanceScore = it.balanceScore,
            depthScore = it.depthScore
        )
    } ?: SceneAnalysisResult.CompositionAnalysis(
        ruleOfThirdsScore = 0f,
        subjectPosition = Pair(0.5f, 0.5f),
        balanceScore = 0f,
        depthScore = 0f
    )
    
    return SceneAnalysisResult(
        sceneType = sceneTypeEnum,
        confidence = confidence,
        lightingCondition = lightingEnum,
        lightingConfidence = lightingConfidence,
        composition = compositionAnalysis,
        detectedObjects = objects.map { it.label },
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Convert PoseAnalysisDto to domain PoseResult
 */
fun PoseAnalysisDto.toDomain(): PoseResult {
    val landmarkMap = landmarks.mapValues { (_, landmark) ->
        Pair(landmark.x, landmark.y)
    }
    
    return PoseResult(
        landmarks = landmarkMap,
        confidence = overallConfidence,
        detectedAt = System.currentTimeMillis()
    )
}

/**
 * Convert domain PoseResult to request format
 */
fun PoseResult.toRequestJson(): String {
    return gson.toJson(landmarks)
}
