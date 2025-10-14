package com.example.poselens.domain.usecase

import com.example.poselens.di.IoDispatcher
import com.example.poselens.domain.model.PoseTemplate
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for managing pose templates
 */
class GetPoseTemplatesUseCase @Inject constructor(
    private val templateRepository: TemplateRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    
    /**
     * Get all available pose templates
     */
    fun getAllTemplates(): Flow<Result<List<PoseTemplate>>> {
        return templateRepository.getAllTemplates()
            .flowOn(dispatcher)
    }
    
    /**
     * Get templates by category
     * @param category Category to filter by
     */
    fun getByCategory(category: String): Flow<Result<List<PoseTemplate>>> {
        return templateRepository.getTemplatesByCategory(category)
            .flowOn(dispatcher)
    }
    
    /**
     * Search templates
     * @param query Search query
     */
    fun search(query: String): Flow<Result<List<PoseTemplate>>> {
        return templateRepository.searchTemplates(query)
            .flowOn(dispatcher)
    }
    
    /**
     * Get favorite templates
     */
    fun getFavorites(): Flow<Result<List<PoseTemplate>>> {
        return templateRepository.getFavoriteTemplates()
            .flowOn(dispatcher)
    }
    
    /**
     * Get recently used templates
     * @param limit Maximum number to return
     */
    fun getRecent(limit: Int = 10): Flow<Result<List<PoseTemplate>>> {
        return templateRepository.getRecentTemplates(limit)
            .flowOn(dispatcher)
    }
    
    /**
     * Toggle favorite status
     * @param templateId Template ID
     * @param isFavorite New favorite status
     */
    suspend fun toggleFavorite(templateId: String, isFavorite: Boolean): Result<Unit> {
        return withContext(dispatcher) {
            templateRepository.toggleFavorite(templateId, isFavorite)
        }
    }
    
    /**
     * Record template usage
     * @param templateId Template ID
     */
    suspend fun recordUsage(templateId: String) {
        withContext(dispatcher) {
            templateRepository.recordTemplateUsage(templateId)
        }
    }
    
    /**
     * Sync templates from server
     */
    suspend fun syncFromServer(): Result<Int> {
        return withContext(dispatcher) {
            templateRepository.syncTemplatesFromServer()
        }
    }
}
