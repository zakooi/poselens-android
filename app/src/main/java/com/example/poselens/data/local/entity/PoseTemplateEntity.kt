package com.example.poselens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Database entity for storing pose templates
 */
@Entity(tableName = "pose_templates")
data class PoseTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val thumbnailUrl: String?,
    val imageUrl: String?,
    val landmarkPositions: String, // JSON string of Map<String, Pair<Float, Float>>
    val confidence: Float,
    val tags: String, // JSON string of List<String>
    val isFavorite: Boolean = false,
    val usageCount: Int = 0,
    val lastUsedTimestamp: Long = 0L,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Database entity for storing analyzed poses
 */
@Entity(tableName = "pose_history")
data class PoseHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,
    val landmarkPositions: String, // JSON string
    val confidence: Float,
    val detectedAt: Long = System.currentTimeMillis(),
    val sceneType: String? = null,
    val suggestions: String? = null, // JSON string of List<String>
    val matchedTemplateId: String? = null,
    val matchScore: Float? = null
)

/**
 * Type converters for Room database
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList())
    }
    
    @TypeConverter
    fun fromLandmarkMap(value: String?): Map<String, Pair<Float, Float>> {
        if (value == null) return emptyMap()
        val mapType = object : TypeToken<Map<String, Pair<Float, Float>>>() {}.type
        return gson.fromJson(value, mapType)
    }
    
    @TypeConverter
    fun toLandmarkMap(map: Map<String, Pair<Float, Float>>?): String {
        return gson.toJson(map ?: emptyMap())
    }
}
