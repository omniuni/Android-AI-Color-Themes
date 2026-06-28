package com.omniimpact.aicolorthemes.di

import android.content.Context
import com.omniimpact.aicolorthemes.utility.IUtilitySettings
import com.omniimpact.aicolorthemes.utility.IUtilityDeepSeekQuery
import com.omniimpact.aicolorthemes.utility.UtilityDeepSeekQuery
import com.omniimpact.aicolorthemes.utility.UtilitySettings
import com.omniimpact.aicolorthemes.network.DeepSeekApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
object AppModule {

	@Provides
	@IoDispatcher
	fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

	@Provides
	@Singleton
	fun provideUtilitySettings(@ApplicationContext context: Context): IUtilitySettings {
		return UtilitySettings(context)
	}

	@Provides
	@Singleton
	fun provideUtilityDeepSeekQuery(
		utilitySettings: IUtilitySettings,
		moshi: Moshi,
		deepSeekApiService: DeepSeekApiService,
		@IoDispatcher ioDispatcher: CoroutineDispatcher
	): IUtilityDeepSeekQuery {
		return UtilityDeepSeekQuery(utilitySettings, moshi, deepSeekApiService, ioDispatcher)
	}

	@Provides
	@Singleton
	fun provideMoshi(): Moshi {
		return Moshi.Builder()
			.addLast(KotlinJsonAdapterFactory())
			.build()
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(): OkHttpClient {
		return OkHttpClient.Builder()
			.connectTimeout(60, TimeUnit.SECONDS)
			.readTimeout(60, TimeUnit.SECONDS)
			.writeTimeout(60, TimeUnit.SECONDS)
			.build()
	}

	@Provides
	@Singleton
	fun provideDeepSeekApiService(moshi: Moshi, okHttpClient: OkHttpClient): DeepSeekApiService {
		return Retrofit.Builder()
			.baseUrl("https://api.deepseek.com/")
			.client(okHttpClient)
			.addConverterFactory(MoshiConverterFactory.create(moshi))
			.build()
			.create(DeepSeekApiService::class.java)
	}
}
