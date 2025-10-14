package com.example.poselens.di

import android.content.Context
import com.example.poselens.data.local.dao.PoseHistoryDao
import com.example.poselens.data.local.dao.PoseTemplateDao
import com.example.poselens.data.ml.ImageAnalyzer
import com.example.poselens.data.ml.PoseEstimator
import com.example.poselens.data.remote.ApiService
import com.example.poselens.data.repository.ImageRepositoryImpl
import com.example.poselens.data.repository.PoseRepositoryImpl
import com.example.poselens.data.repository.PreferencesRepositoryImpl
import com.example.poselens.data.repository.TemplateRepositoryImpl
import com.example.poselens.domain.repository.ImageRepository
import com.example.poselens.domain.repository.PoseRepository
import com.example.poselens.domain.repository.PreferencesRepository
import com.example.poselens.domain.repository.TemplateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

/**
 * Repository Module - Provides repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Provides Image Repository
     */
    @Provides
    @Singleton
    fun provideImageRepository(
        @ApplicationContext context: Context,
        imageAnalyzer: ImageAnalyzer,
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): ImageRepository {
        return ImageRepositoryImpl(context, imageAnalyzer, apiService, dispatcher)
    }
    
    /**
     * Provides Pose Repository
     */
    @Provides
    @Singleton
    fun providePoseRepository(
        @ApplicationContext context: Context,
        poseEstimator: PoseEstimator,
        poseHistoryDao: PoseHistoryDao,
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): PoseRepository {
        return PoseRepositoryImpl(context, poseEstimator, poseHistoryDao, apiService, dispatcher)
    }
    
    /**
     * Provides Template Repository
     */
    @Provides
    @Singleton
    fun provideTemplateRepository(
        templateDao: PoseTemplateDao,
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): TemplateRepository {
        return TemplateRepositoryImpl(templateDao, apiService, dispatcher)
    }
    
    /**
     * Provides Preferences Repository
     */
    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(dataStore)
    }
}
