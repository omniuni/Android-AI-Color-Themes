package com.omniimpact.aicolorthemes.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_themes")
data class ThemeEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	@ColumnInfo(name = "date_created") val dateCreated: Long,
	val title: String,
	val description: String,
	@ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
)
