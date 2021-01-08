package com.example.howie.widget;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.howie.database.WidgetSettings
import com.example.howie.database.WidgetSettingsDao
import com.example.howie.database.getDatabase
import com.example.howie.ui.TasksRepository
import kotlinx.coroutines.launch


class WidgetConfigureViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    private val widgetSettingsDao: WidgetSettingsDao

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
        widgetSettingsDao = database.getWidgetSettingsDao()
    }

    val taskListNames = liveData {
        emit(repository.getTaskListNames())
    }

    val widgetSettings = widgetSettingsDao.getAllWidgetSettings().asLiveData()

    fun insert(widgetId: Int, taskList: Int) = viewModelScope.launch {
        widgetSettingsDao.insert(WidgetSettings(widgetId, taskList))
    }
}
