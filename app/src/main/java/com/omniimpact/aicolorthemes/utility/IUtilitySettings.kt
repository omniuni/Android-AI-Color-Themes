package com.omniimpact.aicolorthemes.utility

import kotlinx.coroutines.flow.Flow

/**
 * Interface for UtilitySettings to enable easy mocking.
 */
interface IUtilitySettings {
	fun getStringFlow(key: String, defaultValue: String): Flow<String>
	suspend fun saveString(key: String, value: String)
	fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean>
	suspend fun saveBoolean(key: String, value: Boolean)
	fun getIntFlow(key: String, defaultValue: Int): Flow<Int>
	suspend fun saveInt(key: String, value: Int)
}
