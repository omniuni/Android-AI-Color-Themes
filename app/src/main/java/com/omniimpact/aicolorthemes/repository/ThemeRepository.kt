package com.omniimpact.aicolorthemes.repository

import androidx.core.graphics.toColorInt
import com.omniimpact.aicolorthemes.database.dao.ThemeDao
import com.omniimpact.aicolorthemes.database.entity.ThemeEntity
import com.omniimpact.aicolorthemes.di.IoDispatcher
import com.omniimpact.aicolorthemes.model.ModelColorTheme
import com.omniimpact.aicolorthemes.model.ModelSingleColor
import com.omniimpact.aicolorthemes.utility.IDeepSeekResult
import com.omniimpact.aicolorthemes.utility.IUtilityDeepSeekQuery
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing color themes data and operations.
 */
@Singleton
class ThemeRepository @Inject constructor(
	private val utilityDeepSeekQuery: IUtilityDeepSeekQuery,
	private val themeDao: ThemeDao,
	@param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

	val themes: StateFlow<List<ModelColorTheme>> = themeDao.getThemesFlow()
		.combine(themeDao.getAllColorsFlow()) { themeEntities, colorEntities ->
			val colorsByThemeId = colorEntities.groupBy { it.keyThemeId }
			themeEntities.map { themeEntity ->
				val colors = colorsByThemeId[themeEntity.id]
					?.sortedBy { it.colorIndex }
					?.map { it.colorHex }
					?: emptyList()
				ModelColorTheme(
					themeName = themeEntity.title,
					themeDescription = themeEntity.description,
					colorTheme = colors,
					id = themeEntity.id
				)
			}
		}
		.flowOn(ioDispatcher)
		.stateIn(
			scope = CoroutineScope(ioDispatcher + SupervisorJob()),
			started = SharingStarted.Eagerly,
			initialValue = emptyList()
		)

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
					
					val themeEntity = ThemeEntity(
						dateCreated = System.currentTimeMillis(),
						title = updatedTheme.themeName,
						description = updatedTheme.themeDescription,
						isFavorite = false
					)
					themeDao.insertThemeWithColors(themeEntity, validColors)
					
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
		CoroutineScope(ioDispatcher).launch {
			theme.id?.let { id ->
				themeDao.deleteThemeWithColors(id)
			}
		}
	}

	/**
	 * Clears all generated themes from the repository.
	 */
	fun clearThemes() {
		CoroutineScope(ioDispatcher).launch {
			themeDao.clearAllThemes()
		}
	}

	/**
	 * Requests a single color query from deep seek.
	 */
	fun getSingleColor(query: ModelSingleColor): Flow<IDeepSeekResult<ModelSingleColor>> {
		return utilityDeepSeekQuery.send(query)
	}
}
