package com.example.poselens.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request DTO for image analysis
 */
@Serializable
data class AnalyzeImageRequest(
    @SerialName("image_base64")
    val imageBase64: String,
    @SerialName("include_pose")
    val includePose: Boolean = true,
    @SerialName("include_scene")
    val includeScene: Boolean = true,
    @SerialName("user_id")
    val userId: String? = null
)

/**
 * Response DTO for image analysis
 */
@Serializable
data class AnalyzeImageResponse(
    @SerialName("analysis_id")
    val analysisId: String,
    @SerialName("scene_analysis")
    val sceneAnalysis: SceneAnalysisDto? = null,
    @SerialName("pose_analysis")
    val poseAnalysis: PoseAnalysisDto? = null,
    @SerialName("suggestions")
    val suggestions: List<String> = emptyList(),
    @SerialName("processing_time_ms")
    val processingTimeMs: Long,
    @SerialName("timestamp")
    val timestamp: Long
)

/**
 * Scene analysis DTO
 */
@Serializable
data class SceneAnalysisDto(
    @SerialName("scene_type")
    val sceneType: String,
    @SerialName("confidence")
    val confidence: Float,
    @SerialName("lighting_condition")
    val lightingCondition: String,
    @SerialName("lighting_confidence")
    val lightingConfidence: Float,
    @SerialName("composition")
    val composition: CompositionDto? = null,
    @SerialName("colors")
    val colors: List<ColorInfoDto> = emptyList(),
    @SerialName("objects")
    val objects: List<ObjectInfoDto> = emptyList()
)

/**
 * Composition analysis DTO
 */
@Serializable
data class CompositionDto(
    @SerialName("rule_of_thirds_score")
    val ruleOfThirdsScore: Float,
    @SerialName("subject_position")
    val subjectPosition: PositionDto,
    @SerialName("balance_score")
    val balanceScore: Float,
    @SerialName("depth_score")
    val depthScore: Float
)

/**
 * Position DTO
 */
@Serializable
data class PositionDto(
    @SerialName("x")
    val x: Float,
    @SerialName("y")
    val y: Float
)

/**
 * Color info DTO
 */
@Serializable
data class ColorInfoDto(
    @SerialName("color")
    val color: String,
    @SerialName("percentage")
    val percentage: Float,
    @SerialName("hex")
    val hex: String
)

/**
 * Object detection DTO
 */
@Serializable
data class ObjectInfoDto(
    @SerialName("label")
    val label: String,
    @SerialName("confidence")
    val confidence: Float,
    @SerialName("bounding_box")
    val boundingBox: BoundingBoxDto? = null
)

/**
 * Bounding box DTO
 */
@Serializable
data class BoundingBoxDto(
    @SerialName("left")
    val left: Float,
    @SerialName("top")
    val top: Float,
    @SerialName("right")
    val right: Float,
    @SerialName("bottom")
    val bottom: Float
)

/**
 * Pose analysis DTO
 */
@Serializable
data class PoseAnalysisDto(
    @SerialName("landmarks")
    val landmarks: Map<String, LandmarkDto>,
    @SerialName("overall_confidence")
    val overallConfidence: Float,
    @SerialName("matched_templates")
    val matchedTemplates: List<TemplateMatchDto> = emptyList(),
    @SerialName("pose_quality_score")
    val poseQualityScore: Float,
    @SerialName("suggestions")
    val suggestions: List<String> = emptyList()
)

/**
 * Landmark DTO
 */
@Serializable
data class LandmarkDto(
    @SerialName("x")
    val x: Float,
    @SerialName("y")
    val y: Float,
    @SerialName("z")
    val z: Float? = null,
    @SerialName("confidence")
    val confidence: Float
)

/**
 * Template match DTO
 */
@Serializable
data class TemplateMatchDto(
    @SerialName("template_id")
    val templateId: String,
    @SerialName("template_name")
    val templateName: String,
    @SerialName("similarity_score")
    val similarityScore: Float,
    @SerialName("category")
    val category: String
)

/**
 * Pose template DTO
 */
@Serializable
data class PoseTemplateDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("category")
    val category: String,
    @SerialName("difficulty")
    val difficulty: String,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("landmarks")
    val landmarks: Map<String, LandmarkDto>,
    @SerialName("tags")
    val tags: List<String> = emptyList(),
    @SerialName("created_at")
    val createdAt: Long,
    @SerialName("updated_at")
    val updatedAt: Long
)

/**
 * Templates list response
 */
@Serializable
data class TemplatesResponse(
    @SerialName("templates")
    val templates: List<PoseTemplateDto>,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("page")
    val page: Int,
    @SerialName("page_size")
    val pageSize: Int
)

/**
 * Generic API response wrapper
 */
@Serializable
data class ApiResponse<T>(
    @SerialName("success")
    val success: Boolean,
    @SerialName("data")
    val data: T? = null,
    @SerialName("error")
    val error: ApiError? = null,
    @SerialName("timestamp")
    val timestamp: Long
)

/**
 * API error DTO
 */
@Serializable
data class ApiError(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("details")
    val details: String? = null
)
