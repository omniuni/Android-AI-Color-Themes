package com.omniimpact.aicolorthemes.utility

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

/**
 * Utility class for managing application settings and preferences.
 * This class provides a wrapper around Android DataStore for saving and retrieving
 * various data types synchronously using runBlocking.
 *
 * Although it is possible and generally recommended to use DataStore asynchronously,
 * this provides a simple way to interface with it for very light use cases.
 *
 * @param context The application context used to access DataStore.
 */
class UtilitySettings(private val context: Context) {

	/**
	 * Saves a string value associated with a specific key.
	 *
	 * @param key The key to associate the value with.
	 * @param value The string value to save.
	 */
	fun saveString(key: String, value: String) {
		runBlocking {
			context.dataStore.edit { settings ->
				settings[stringPreferencesKey(key)] = value
			}
		}
	}

	/**
	 * Retrieves a string value associated with a specific key.
	 *
	 * @param key The key to look up.
	 * @param defaultValue The value to return if the key is not found.
	 * @return The saved string value or [defaultValue] if not present.
	 */
	fun getString(key: String, defaultValue: String): String {
		return runBlocking {
			val preferences = context.dataStore.data.first()
			preferences[stringPreferencesKey(key)] ?: defaultValue
		}
	}

	/**
	 * Saves a boolean value associated with a specific key.
	 *
	 * @param key The key to associate the value with.
	 * @param value The boolean value to save.
	 */
	fun saveBoolean(key: String, value: Boolean) {
		runBlocking {
			context.dataStore.edit { settings ->
				settings[booleanPreferencesKey(key)] = value
			}
		}
	}

	/**
	 * Retrieves a boolean value associated with a specific key.
	 *
	 * @param key The key to look up.
	 * @param defaultValue The value to return if the key is not found.
	 * @return The saved boolean value or [defaultValue] if not present.
	 */
	fun getBoolean(key: String, defaultValue: Boolean): Boolean {
		return runBlocking {
			val preferences = context.dataStore.data.first()
			preferences[booleanPreferencesKey(key)] ?: defaultValue
		}
	}

	/**
	 * Saves an integer value associated with a specific key.
	 *
	 * @param key The key to associate the value with.
	 * @param value The integer value to save.
	 */
	fun saveInt(key: String, value: Int) {
		runBlocking {
			context.dataStore.edit { settings ->
				settings[intPreferencesKey(key)] = value
			}
		}
	}

	/**
	 * Retrieves an integer value associated with a specific key.
	 *
	 * @param key The key to look up.
	 * @param defaultValue The value to return if the key is not found.
	 * @return The saved integer value or [defaultValue] if not present.
	 */
	fun getInt(key: String, defaultValue: Int): Int {
		return runBlocking {
			val preferences = context.dataStore.data.first()
			preferences[intPreferencesKey(key)] ?: defaultValue
		}
	}
}
