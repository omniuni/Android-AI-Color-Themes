package com.omniimpact.aicolorthemes.utility

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

/**
 * Utility class for managing application settings and preferences asynchronously.
 * This class provides a wrapper around Android DataStore using Flow and suspending functions.
 *
 * @param context The application context used to access DataStore.
 */
class UtilitySettings(private val context: Context) : IUtilitySettings {

	/**
	 * Retrieves a Flow of a string value associated with a specific key.
	 */
	override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
		return context.dataStore.data.map { preferences ->
			preferences[stringPreferencesKey(key)] ?: defaultValue
		}
	}

	/**
	 * Saves a string value associated with a specific key asynchronously.
	 *
	 * @param key The key to associate the value with.
	 * @param value The string value to save.
	 */
	override suspend fun saveString(key: String, value: String) {
		context.dataStore.edit { settings ->
			settings[stringPreferencesKey(key)] = value
		}
	}

	/**
	 * Retrieves a Flow of a boolean value associated with a specific key.
	 */
	override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
		return context.dataStore.data.map { preferences ->
			preferences[booleanPreferencesKey(key)] ?: defaultValue
		}
	}

	/**
	 * Saves a boolean value associated with a specific key asynchronously.
	 *
	 * @param key The key to associate the value with.
	 * @param value The boolean value to save.
	 */
	override suspend fun saveBoolean(key: String, value: Boolean) {
		context.dataStore.edit { settings ->
			settings[booleanPreferencesKey(key)] = value
		}
	}

	/**
	 * Retrieves a Flow of an integer value associated with a specific key.
	 */
	override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> {
		return context.dataStore.data.map { preferences ->
			preferences[intPreferencesKey(key)] ?: defaultValue
		}
	}

	/**
	 * Saves an integer value associated with a specific key asynchronously.
	 *
	 * @param key The key to associate the value with.
	 * @param value The integer value to save.
	 */
	override suspend fun saveInt(key: String, value: Int) {
		context.dataStore.edit { settings ->
			settings[intPreferencesKey(key)] = value
		}
	}
}
