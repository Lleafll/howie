package com.lorenz.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.lorenz.howie.core.TaskIndex
import com.lorenz.howie.core.TaskListIndex
import com.lorenz.howie.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MoveTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    var taskId by Delegates.notNull<TaskIndex>()
    var fromTaskList by Delegates.notNull<TaskListIndex>()

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(
            buildDefaultWidgetUpdater(application),
            database.getTaskDao(),
            database.getTaskListDao()
        )
    }

    val taskListNames = liveData {
        emit(repository.getTaskListNames())
    }

    fun moveToList(toList: TaskListIndex) = viewModelScope.launch {
        repository.moveTaskFromListToList(taskId, fromTaskList, toList)
    }
}
