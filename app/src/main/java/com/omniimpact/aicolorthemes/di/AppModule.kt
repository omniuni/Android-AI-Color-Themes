package com.omniimpact.aicolorthemes.di

import android.content.Context
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
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideUtilitySettings(@ApplicationContext context: Context): UtilitySettings {
		return UtilitySettings(context)
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
