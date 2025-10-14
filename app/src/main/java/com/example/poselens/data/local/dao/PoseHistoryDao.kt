package com.example.poselens.data.local.dao

import androidx.room.*
import com.example.poselens.data.local.entity.PoseHistoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for pose history
 */
@Dao
interface PoseHistoryDao {
    
    @Query("SELECT * FROM pose_history ORDER BY detectedAt DESC")
    fun getAllHistory(): Flow<List<PoseHistoryEntity>>
    
    @Query("SELECT * FROM pose_history ORDER BY detectedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 20): Flow<List<PoseHistoryEntity>>
    
    @Query("SELECT * FROM pose_history WHERE id = :historyId")
    suspend fun getHistoryById(historyId: Long): PoseHistoryEntity?
    
    @Query("SELECT * FROM pose_history WHERE sceneType = :sceneType ORDER BY detectedAt DESC")
    fun getHistoryBySceneType(sceneType: String): Flow<List<PoseHistoryEntity>>
    
    @Query("SELECT * FROM pose_history WHERE matchedTemplateId = :templateId ORDER BY detectedAt DESC")
    fun getHistoryByTemplate(templateId: String): Flow<List<PoseHistoryEntity>>
    
    @Query("SELECT * FROM pose_history WHERE detectedAt >= :startTime ORDER BY detectedAt DESC")
    fun getHistorySince(startTime: Long): Flow<List<PoseHistoryEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PoseHistoryEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryList(historyList: List<PoseHistoryEntity>)
    
    @Update
    suspend fun updateHistory(history: PoseHistoryEntity)
    
    @Delete
    suspend fun deleteHistory(history: PoseHistoryEntity)
    
    @Query("DELETE FROM pose_history WHERE id = :historyId")
    suspend fun deleteHistoryById(historyId: Long)
    
    @Query("DELETE FROM pose_history")
    suspend fun deleteAllHistory()
    
    @Query("DELETE FROM pose_history WHERE detectedAt < :timestamp")
    suspend fun deleteHistoryOlderThan(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM pose_history")
    suspend fun getHistoryCount(): Int
}
