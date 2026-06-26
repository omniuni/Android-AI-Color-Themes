package com.omniimpact.aicolorthemes.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omniimpact.aicolorthemes.database.dao.ThemeDao
import com.omniimpact.aicolorthemes.database.entity.ThemeColorEntity
import com.omniimpact.aicolorthemes.database.entity.ThemeEntity

@Database(entities = [ThemeEntity::class, ThemeColorEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
	abstract fun themeDao(): ThemeDao
}
