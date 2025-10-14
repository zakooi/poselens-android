package com.example.poselens.data.repository

import com.example.poselens.data.local.dao.PoseTemplateDao
import com.example.poselens.data.mapper.toDomain
import com.example.poselens.data.mapper.toEntity
import com.example.poselens.data.remote.ApiService
import com.example.poselens.domain.model.PoseTemplate
import com.example.poselens.domain.model.Result
import com.example.poselens.domain.repository.TemplateRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateRepositoryImpl @Inject constructor(
    private val templateDao: PoseTemplateDao,
    private val apiService: ApiService,
    private val ioDispatcher: CoroutineDispatcher
) : TemplateRepository {
    
    override fun getAllTemplates(): Flow<Result<List<PoseTemplate>>> {
        return templateDao.getAllTemplates()
            .map<List<com.example.poselens.data.local.entity.PoseTemplateEntity>, Result<List<PoseTemplate>>> { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.Error(e as? Exception ?: Exception(e), "Failed to load templates: ${e.message}"))
            }
            .flowOn(ioDispatcher)
    }
    
    override fun getTemplatesByCategory(category: String): Flow<Result<List<PoseTemplate>>> {
        return templateDao.getTemplatesByCategory(category)
            .map<List<com.example.poselens.data.local.entity.PoseTemplateEntity>, Result<List<PoseTemplate>>> { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.Error(e as? Exception ?: Exception(e), "Failed to load templates: ${e.message}"))
            }
            .flowOn(ioDispatcher)
    }
    
    override suspend fun getTemplateById(templateId: String): Result<PoseTemplate> {
        return withContext(ioDispatcher) {
            try {
                val entity = templateDao.getTemplateById(templateId)
                    ?: return@withContext Result.Error(
                        Exception("Template not found"),
                        "Template with ID $templateId not found"
                    )
                Result.Success(entity.toDomain())
            } catch (e: Exception) {
                Result.Error(e, "Failed to get template: ${e.message}")
            }
        }
    }
    
    override fun searchTemplates(query: String): Flow<Result<List<PoseTemplate>>> {
        return templateDao.searchTemplates(query)
            .map<List<com.example.poselens.data.local.entity.PoseTemplateEntity>, Result<List<PoseTemplate>>> { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.Error(e as? Exception ?: Exception(e), "Failed to search templates: ${e.message}"))
            }
            .flowOn(ioDispatcher)
    }
    
    override fun getFavoriteTemplates(): Flow<Result<List<PoseTemplate>>> {
        return templateDao.getFavoriteTemplates()
            .map<List<com.example.poselens.data.local.entity.PoseTemplateEntity>, Result<List<PoseTemplate>>> { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.Error(e as? Exception ?: Exception(e), "Failed to load favorites: ${e.message}"))
            }
            .flowOn(ioDispatcher)
    }
    
    override suspend fun toggleFavorite(templateId: String, isFavorite: Boolean): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                templateDao.updateFavoriteStatus(templateId, isFavorite)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e, "Failed to update favorite: ${e.message}")
            }
        }
    }
    
    override fun getRecentTemplates(limit: Int): Flow<Result<List<PoseTemplate>>> {
        return templateDao.getRecentTemplates(limit)
            .map<List<com.example.poselens.data.local.entity.PoseTemplateEntity>, Result<List<PoseTemplate>>> { entities ->
                Result.Success(entities.map { it.toDomain() })
            }
            .catch { e ->
                emit(Result.Error(e as? Exception ?: Exception(e), "Failed to load recent templates: ${e.message}"))
            }
            .flowOn(ioDispatcher)
    }
    
    override suspend fun recordTemplateUsage(templateId: String) {
        withContext(ioDispatcher) {
            try {
                templateDao.incrementUsageCount(templateId)
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }
    
    override suspend fun syncTemplatesFromServer(): Result<Int> {
        return withContext(ioDispatcher) {
            try {
                val response = apiService.getTemplates(page = 1, pageSize = 100)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val templates = response.body()?.data?.templates ?: emptyList()
                    
                    // Convert to entities and insert
                    val entities = templates.map { dto ->
                        dto.toDomain().toEntity()
                    }
                    
                    templateDao.insertTemplates(entities)
                    Result.Success(entities.size)
                } else {
                    Result.Error(
                        Exception("API error: ${response.code()}"),
                        response.body()?.error?.message ?: "Failed to sync templates"
                    )
                }
            } catch (e: Exception) {
                Result.Error(e, "Failed to sync templates: ${e.message}")
            }
        }
    }
}
