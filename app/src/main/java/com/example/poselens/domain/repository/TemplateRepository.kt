package com.example.poselens.domain.repository

import com.example.poselens.domain.model.PoseTemplate
import com.example.poselens.domain.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for pose template management
 */
interface TemplateRepository {
    
    /**
     * Get all available pose templates
     * @return Flow emitting list of templates
     */
    fun getAllTemplates(): Flow<Result<List<PoseTemplate>>>
    
    /**
     * Get templates by category
     * @param category Category to filter by
     * @return Flow emitting list of templates
     */
    fun getTemplatesByCategory(category: String): Flow<Result<List<PoseTemplate>>>
    
    /**
     * Get a specific template by ID
     * @param templateId Template ID
     * @return Result with template
     */
    suspend fun getTemplateById(templateId: String): Result<PoseTemplate>
    
    /**
     * Search templates by name or tags
     * @param query Search query
     * @return Flow emitting search results
     */
    fun searchTemplates(query: String): Flow<Result<List<PoseTemplate>>>
    
    /**
     * Get favorite templates
     * @return Flow emitting favorite templates
     */
    fun getFavoriteTemplates(): Flow<Result<List<PoseTemplate>>>
    
    /**
     * Mark template as favorite
     * @param templateId Template ID
     * @param isFavorite Whether to favorite or unfavorite
     * @return Result with success
     */
    suspend fun toggleFavorite(templateId: String, isFavorite: Boolean): Result<Unit>
    
    /**
     * Get recently used templates
     * @param limit Maximum number of templates to return
     * @return Flow emitting recent templates
     */
    fun getRecentTemplates(limit: Int = 10): Flow<Result<List<PoseTemplate>>>
    
    /**
     * Record template usage for analytics
     * @param templateId Template ID
     */
    suspend fun recordTemplateUsage(templateId: String)
    
    /**
     * Sync templates from server
     * @return Result with number of synced templates
     */
    suspend fun syncTemplatesFromServer(): Result<Int>
}
