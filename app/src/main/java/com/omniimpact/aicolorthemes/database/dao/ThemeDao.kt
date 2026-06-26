package com.omniimpact.aicolorthemes.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.omniimpact.aicolorthemes.database.entity.ThemeColorEntity
import com.omniimpact.aicolorthemes.database.entity.ThemeEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ThemeDao {
	@Insert
	abstract suspend fun insertTheme(theme: ThemeEntity): Long

	@Insert
	abstract suspend fun insertThemeColors(colors: List<ThemeColorEntity>)

	@Transaction
	open suspend fun insertThemeWithColors(theme: ThemeEntity, colors: List<String>) {
		val themeId = insertTheme(theme)
		val colorEntities = colors.mapIndexed { index, hex ->
			ThemeColorEntity(keyThemeId = themeId, colorIndex = index, colorHex = hex)
		}
		insertThemeColors(colorEntities)
	}

	@Query("SELECT * FROM table_themes ORDER BY date_created DESC")
	abstract fun getThemesFlow(): Flow<List<ThemeEntity>>

	@Query("SELECT * FROM table_theme_colors WHERE key_theme_id = :themeId ORDER BY color_index ASC")
	abstract suspend fun getColorsForTheme(themeId: Long): List<ThemeColorEntity>

	@Query("SELECT * FROM table_theme_colors ORDER BY key_theme_id, color_index ASC")
	abstract fun getAllColorsFlow(): Flow<List<ThemeColorEntity>>

	@Delete
	abstract suspend fun deleteTheme(theme: ThemeEntity)

	@Query("DELETE FROM table_themes WHERE id = :id")
	abstract suspend fun deleteThemeById(id: Long)

	@Query("DELETE FROM table_theme_colors WHERE key_theme_id = :themeId")
	abstract suspend fun deleteColorsByThemeId(themeId: Long)

	@Transaction
	open suspend fun deleteThemeWithColors(themeId: Long) {
		deleteColorsByThemeId(themeId)
		deleteThemeById(themeId)
	}

	@Query("DELETE FROM table_themes")
	abstract suspend fun clearAllThemes()

	@Query("UPDATE table_themes SET is_favorite = :isFavorite WHERE id = :themeId")
	abstract suspend fun updateFavoriteStatus(themeId: Long, isFavorite: Boolean)
}
