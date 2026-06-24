package com.omniimpact.aicolorthemes.utility

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class UtilitySettings(private val context: Context) {

	fun saveString(key: String, value: String) {
		runBlocking {
			context.dataStore.edit { settings ->
				settings[stringPreferencesKey(key)] = value
			}
		}
	}

	fun getString(key: String, defaultValue: String): String {
		return runBlocking {
			val preferences = context.dataStore.data.first()
			preferences[stringPreferencesKey(key)] ?: defaultValue
		}
	}

	fun saveBoolean(key: String, value: Boolean) {
		runBlocking {
			context.dataStore.edit { settings ->
				settings[booleanPreferencesKey(key)] = value
			}
		}
	}

	fun getBoolean(key: String, defaultValue: Boolean): Boolean {
		return runBlocking {
			val preferences = context.dataStore.data.first()
			preferences[booleanPreferencesKey(key)] ?: defaultValue
		}
	}
}
