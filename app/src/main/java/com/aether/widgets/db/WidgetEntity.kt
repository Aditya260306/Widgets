package com.aether.widgets.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "widgets")
data class WidgetEntity(
    @PrimaryKey val widgetId: Int,
    val type: String,
    val content: String,
    val timestamp: Long,
    val instructions: String? = null
)
