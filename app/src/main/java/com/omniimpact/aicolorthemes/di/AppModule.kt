package com.omniimpact.aicolorthemes.di

import android.content.Context
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUtilitySettings(@ApplicationContext context: Context): UtilitySettings {
        return UtilitySettings(context)
    }
}
