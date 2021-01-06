package com.example.howie;

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WidgetConfigureViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    private val widgetSettingsDao: WidgetSettingsDao

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
        widgetSettingsDao = database.getWidgetSettingsDao()
    }

    val taskLists = repository.taskLists
    val widgetSettings = widgetSettingsDao.getAllWidgetSettings().asLiveData()

    fun insert(widgetSettings: WidgetSettings) = viewModelScope.launch {
        widgetSettingsDao.insert(widgetSettings)
    }
}
