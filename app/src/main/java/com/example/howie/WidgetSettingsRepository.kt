package com.example.howie

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WidgetSettingsRepository(private val widgetSettingsDao: WidgetSettingsDao) : ViewModel() {
    fun getWidgetSettings(widgetId: Int) = widgetSettingsDao.getWidgetSettings(widgetId)

    suspend fun insert(widgetSettings: WidgetSettings) {
        widgetSettingsDao.insert(widgetSettings)
    }

    fun delete(widgetId: Int) = viewModelScope.launch {
        widgetSettingsDao.delete(widgetId)
    }

    companion object {
        private var instance: WidgetSettingsRepository? = null

        fun getInstance(applicationContext: Context): WidgetSettingsRepository {
            if (instance == null) {
                val database = TasksDatabaseSingleton.getDatabase(applicationContext)
                instance = WidgetSettingsRepository(database.getWidgetSettingsDao())
            }
            return instance!!
        }
    }
}