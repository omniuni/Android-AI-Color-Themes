package com.omniimpact.aicolorthemes.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
	tableName = "table_theme_colors",
	foreignKeys = [
		ForeignKey(
			entity = ThemeEntity::class,
			parentColumns = ["id"],
			childColumns = ["key_theme_id"],
			onDelete = ForeignKey.CASCADE
		)
	],
	indices = [Index(value = ["key_theme_id"])]
)
data class ThemeColorEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	@ColumnInfo(name = "key_theme_id") val keyThemeId: Long,
	@ColumnInfo(name = "color_index") val colorIndex: Int,
	@ColumnInfo(name = "color_hex") val colorHex: String
)
