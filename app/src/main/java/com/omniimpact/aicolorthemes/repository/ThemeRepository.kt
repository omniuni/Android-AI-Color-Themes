package com.omniimpact.aicolorthemes.repository

import androidx.core.graphics.toColorInt
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.UtilityDeepSeekQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing color themes data and operations.
 */
@Singleton
class ThemeRepository @Inject constructor(
	private val utilityDeepSeekQuery: UtilityDeepSeekQuery
) {

	private val _themes = MutableStateFlow<List<ModelColorTheme>>(emptyList())
	val themes: StateFlow<List<ModelColorTheme>> = _themes.asStateFlow()

	/**
	 * Requests a new color theme from the deep seek service and updates the themes array.
	 */
	fun createTheme(query: ModelColorTheme): Flow<IDeepSeekResult<ModelColorTheme>> = flow {
		utilityDeepSeekQuery.send(query).collect { result ->
			when (result) {
				is IDeepSeekResult.Loading -> {
					emit(IDeepSeekResult.Loading)
				}
				is IDeepSeekResult.Success -> {
					val validColors = result.data.colorTheme.filter { colorHex ->
						try {
							colorHex.toColorInt()
							true
						} catch (_: Exception) {
							false
						}
					}
					val updatedTheme = result.data.copy(colorTheme = validColors)
					_themes.update { listOf(updatedTheme) + it }
					emit(IDeepSeekResult.Success(updatedTheme))
				}
				is IDeepSeekResult.Failure -> {
					emit(IDeepSeekResult.Failure(result.message))
				}
			}
		}
	}

	/**
	 * Removes a specific color theme from the repository array.
	 */
	fun removeTheme(theme: ModelColorTheme) {
		_themes.update { it.filter { t -> t != theme } }
	}

	/**
	 * Clears all generated themes from the repository.
	 */
	fun clearThemes() {
		_themes.value = emptyList()
	}

	/**
	 * Requests a single color query from deep seek.
	 */
	fun getSingleColor(query: ModelSingleColor): Flow<IDeepSeekResult<ModelSingleColor>> {
		return utilityDeepSeekQuery.send(query)
	}
}
