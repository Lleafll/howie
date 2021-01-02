package com.example.howie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetSettingsDao {
    @Query("SELECT * from WidgetSettings WHERE widgetId = :widgetId")
    fun getWidgetSettings(widgetId: Int): Flow<WidgetSettings?>

    @Insert
    suspend fun insert(widgetSettings: WidgetSettings)
}