package com.example.poselens.data.local.dao

import androidx.room.*
import com.example.poselens.data.local.entity.PoseTemplateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for pose templates
 */
@Dao
interface PoseTemplateDao {
    
    @Query("SELECT * FROM pose_templates ORDER BY name ASC")
    fun getAllTemplates(): Flow<List<PoseTemplateEntity>>
    
    @Query("SELECT * FROM pose_templates WHERE category = :category ORDER BY name ASC")
    fun getTemplatesByCategory(category: String): Flow<List<PoseTemplateEntity>>
    
    @Query("SELECT * FROM pose_templates WHERE id = :templateId")
    suspend fun getTemplateById(templateId: String): PoseTemplateEntity?
    
    @Query("SELECT * FROM pose_templates WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchTemplates(query: String): Flow<List<PoseTemplateEntity>>
    
    @Query("SELECT * FROM pose_templates WHERE isFavorite = 1 ORDER BY lastUsedTimestamp DESC")
    fun getFavoriteTemplates(): Flow<List<PoseTemplateEntity>>
    
    @Query("SELECT * FROM pose_templates WHERE lastUsedTimestamp > 0 ORDER BY lastUsedTimestamp DESC LIMIT :limit")
    fun getRecentTemplates(limit: Int): Flow<List<PoseTemplateEntity>>
    
    @Query("UPDATE pose_templates SET isFavorite = :isFavorite, updatedAt = :timestamp WHERE id = :templateId")
    suspend fun updateFavoriteStatus(templateId: String, isFavorite: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE pose_templates SET usageCount = usageCount + 1, lastUsedTimestamp = :timestamp, updatedAt = :timestamp WHERE id = :templateId")
    suspend fun incrementUsageCount(templateId: String, timestamp: Long = System.currentTimeMillis())
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: PoseTemplateEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<PoseTemplateEntity>)
    
    @Update
    suspend fun updateTemplate(template: PoseTemplateEntity)
    
    @Delete
    suspend fun deleteTemplate(template: PoseTemplateEntity)
    
    @Query("DELETE FROM pose_templates WHERE id = :templateId")
    suspend fun deleteTemplateById(templateId: String)
    
    @Query("DELETE FROM pose_templates WHERE isCustom = 0")
    suspend fun deleteAllNonCustomTemplates()
    
    @Query("SELECT COUNT(*) FROM pose_templates")
    suspend fun getTemplateCount(): Int
}
