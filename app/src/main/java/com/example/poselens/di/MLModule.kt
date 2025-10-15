package com.example.poselens.di

import android.content.Context
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ML Module - Provides ML Kit and TensorFlow Lite dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object MLModule {
    
    /**
     * Provides ML Kit Pose Detector Options
     */
    @Provides
    @Singleton
    fun providePoseDetectorOptions(): PoseDetectorOptions {
        return PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
    }
    
    /**
     * Provides ML Kit Pose Detector
     */
    @Provides
    @Singleton
    fun providePoseDetector(
        options: PoseDetectorOptions
    ): PoseDetector {
        return PoseDetection.getClient(options)
    }
    
    /**
     * Provides Image Labeler Options
     */
    @Provides
    @Singleton
    fun provideImageLabelerOptions(): ImageLabelerOptions {
        return ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
    }
    
    /**
     * Provides ML Kit Image Labeler
     */
    @Provides
    @Singleton
    fun provideImageLabeler(
        options: ImageLabelerOptions
    ): ImageLabeler {
        return ImageLabeling.getClient(options)
    }
}
