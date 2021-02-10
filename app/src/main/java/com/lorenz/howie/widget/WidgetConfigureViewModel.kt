package com.lorenz.howie.widget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.lorenz.howie.database.WidgetSettings
import com.lorenz.howie.database.WidgetSettingsDao
import com.lorenz.howie.database.getDatabase
import com.lorenz.howie.ui.TasksRepository
import com.lorenz.howie.ui.buildDefaultWidgetUpdater
import kotlinx.coroutines.launch


class WidgetConfigureViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    private val widgetSettingsDao: WidgetSettingsDao

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(
            buildDefaultWidgetUpdater(application),
            database.getTaskDao(),
            database.getTaskListDao()
        )
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
