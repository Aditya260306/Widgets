package com.aether.widgets.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WidgetEntity::class], version = 1, exportSchema = false)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun widgetDao(): WidgetDao
}
