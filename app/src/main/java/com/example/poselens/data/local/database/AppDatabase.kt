package com.example.poselens.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.poselens.data.local.dao.PoseHistoryDao
import com.example.poselens.data.local.dao.PoseTemplateDao
import com.example.poselens.data.local.entity.Converters
import com.example.poselens.data.local.entity.PoseHistoryEntity
import com.example.poselens.data.local.entity.PoseTemplateEntity

/**
 * Main Room database for PoseLens app
 */
@Database(
    entities = [
        PoseTemplateEntity::class,
        PoseHistoryEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun poseTemplateDao(): PoseTemplateDao
    abstract fun poseHistoryDao(): PoseHistoryDao
    
    companion object {
        const val DATABASE_NAME = "poselens_database"
    }
}
