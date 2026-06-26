package com.omniimpact.aicolorthemes.di

import android.content.Context
import androidx.room.Room
import com.omniimpact.aicolorthemes.database.AppDatabase
import com.omniimpact.aicolorthemes.database.dao.ThemeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
		return Room.databaseBuilder(
			context,
			AppDatabase::class.java,
			"ai_color_themes.db"
		).build()
	}

	@Provides
	@Singleton
	fun provideThemeDao(database: AppDatabase): ThemeDao {
		return database.themeDao()
	}
}
