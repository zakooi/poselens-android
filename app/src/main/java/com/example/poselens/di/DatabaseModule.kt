package com.example.poselens.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module - Provides Room database and DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    private const val DATABASE_NAME = "poselens_database"
    
    /**
     * Provides Room Database
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): com.example.poselens.data.local.database.AppDatabase {
        return Room.databaseBuilder(
            context,
            com.example.poselens.data.local.database.AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Provides Pose History DAO
     */
    @Provides
    @Singleton
    fun providePoseHistoryDao(
        database: com.example.poselens.data.local.database.AppDatabase
    ): com.example.poselens.data.local.dao.PoseHistoryDao {
        return database.poseHistoryDao()
    }
    
    /**
     * Provides Template DAO
     */
    @Provides
    @Singleton
    fun providePoseTemplateDao(
        database: com.example.poselens.data.local.database.AppDatabase
    ): com.example.poselens.data.local.dao.PoseTemplateDao {
        return database.poseTemplateDao()
    }
}
