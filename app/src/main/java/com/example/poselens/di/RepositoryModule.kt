package com.example.poselens.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository Module - Provides repository implementations
 * Will be uncommented when repositories are implemented
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    /**
     * Provides Image Repository
     * Uncomment when ImageRepository is implemented
     */
    // @Provides
    // @Singleton
    // fun provideImageRepository(
    //     imageAnalyzer: ImageAnalyzer,
    //     apiService: ApiService,
    //     @IoDispatcher dispatcher: CoroutineDispatcher
    // ): ImageRepository {
    //     return ImageRepositoryImpl(imageAnalyzer, apiService, dispatcher)
    // }
    
    /**
     * Provides Pose Repository
     * Uncomment when PoseRepository is implemented
     */
    // @Provides
    // @Singleton
    // fun providePoseRepository(
    //     poseEstimator: PoseEstimator,
    //     poseDao: PoseDao,
    //     apiService: ApiService,
    //     @IoDispatcher dispatcher: CoroutineDispatcher
    // ): PoseRepository {
    //     return PoseRepositoryImpl(poseEstimator, poseDao, apiService, dispatcher)
    // }
    
    /**
     * Provides Template Repository
     * Uncomment when TemplateRepository is implemented
     */
    // @Provides
    // @Singleton
    // fun provideTemplateRepository(
    //     templateDao: TemplateDao,
    //     apiService: ApiService,
    //     @IoDispatcher dispatcher: CoroutineDispatcher
    // ): TemplateRepository {
    //     return TemplateRepositoryImpl(templateDao, apiService, dispatcher)
    // }
    
    /**
     * Provides Preferences Repository
     * Uncomment when PreferencesRepository is implemented
     */
    // @Provides
    // @Singleton
    // fun providePreferencesRepository(
    //     dataStore: DataStore<Preferences>,
    //     @IoDispatcher dispatcher: CoroutineDispatcher
    // ): PreferencesRepository {
    //     return PreferencesRepositoryImpl(dataStore, dispatcher)
    // }
}
