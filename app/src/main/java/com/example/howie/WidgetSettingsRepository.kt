package com.example.howie

import android.content.Context

class WidgetSettingsRepository(private val widgetSettingsDao: WidgetSettingsDao) {
    fun getWidgetSettings(widgetId: Int) = widgetSettingsDao.getWidgetSettings(widgetId)

    suspend fun insert(widgetSettings: WidgetSettings) {
        widgetSettingsDao.insert(widgetSettings)
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