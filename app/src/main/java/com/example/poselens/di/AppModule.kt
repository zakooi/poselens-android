package com.example.poselens.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App Module - Provides application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides DataStore for preferences
     */
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "poselens_preferences"
    )
    
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }
    
    /**
     * Provides Application Context
     */
    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context {
        return context
    }
}
