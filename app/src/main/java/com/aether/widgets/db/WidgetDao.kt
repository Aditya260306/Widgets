package com.aether.widgets.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widgets WHERE widgetId = :widgetId")
    fun getWidgetById(widgetId: Int): Flow<WidgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity)

    @Query("DELETE FROM widgets WHERE widgetId = :widgetId")
    suspend fun deleteWidget(widgetId: Int)
}
