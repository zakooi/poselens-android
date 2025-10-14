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
     * Uncomment when AppDatabase is implemented
     */
    // @Provides
    // @Singleton
    // fun provideAppDatabase(
    //     @ApplicationContext context: Context
    // ): AppDatabase {
    //     return Room.databaseBuilder(
    //         context,
    //         AppDatabase::class.java,
    //         DATABASE_NAME
    //     )
    //         .fallbackToDestructiveMigration()
    //         .build()
    // }
    
    /**
     * Provides Pose DAO
     * Uncomment when PoseDao is implemented
     */
    // @Provides
    // @Singleton
    // fun providePoseDao(database: AppDatabase): PoseDao {
    //     return database.poseDao()
    // }
    
    /**
     * Provides Template DAO
     * Uncomment when TemplateDao is implemented
     */
    // @Provides
    // @Singleton
    // fun provideTemplateDao(database: AppDatabase): TemplateDao {
    //     return database.templateDao()
    // }
}
