package com.example.poselens.data.remote

import com.example.poselens.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service for PoseLens backend
 */
interface ApiService {
    
    /**
     * Analyze an image for scene and pose detection
     */
    @POST("analyze")
    suspend fun analyzeImage(
        @Body request: AnalyzeImageRequest
    ): Response<ApiResponse<AnalyzeImageResponse>>
    
    /**
     * Get all available pose templates
     */
    @GET("templates")
    suspend fun getTemplates(
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 50
    ): Response<ApiResponse<TemplatesResponse>>
    
    /**
     * Get a specific template by ID
     */
    @GET("templates/{id}")
    suspend fun getTemplateById(
        @Path("id") templateId: String
    ): Response<ApiResponse<PoseTemplateDto>>
    
    /**
     * Search templates
     */
    @GET("templates/search")
    suspend fun searchTemplates(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): Response<ApiResponse<TemplatesResponse>>
    
    /**
     * Get pose suggestions for a scene type
     */
    @GET("suggestions")
    suspend fun getPoseSuggestions(
        @Query("scene_type") sceneType: String,
        @Query("current_pose") currentPoseJson: String? = null
    ): Response<ApiResponse<List<String>>>
    
    /**
     * Upload feedback for analytics
     */
    @POST("feedback")
    suspend fun submitFeedback(
        @Body feedback: Map<String, Any>
    ): Response<ApiResponse<Unit>>
    
    /**
     * Get user statistics
     */
    @GET("user/stats")
    suspend fun getUserStats(
        @Query("user_id") userId: String
    ): Response<ApiResponse<Map<String, Any>>>
    
    /**
     * Health check endpoint
     */
    @GET("health")
    suspend fun healthCheck(): Response<ApiResponse<Map<String, String>>>
}
