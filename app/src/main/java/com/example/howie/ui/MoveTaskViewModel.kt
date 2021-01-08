package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MoveTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    var taskId by Delegates.notNull<Int>()
    var fromTaskList by Delegates.notNull<Int>()

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    val taskListNames = liveData {
        emit(repository.getTaskListNames())
    }

    fun moveToList(toList: Int) = viewModelScope.launch {
        repository.moveTaskFromListToList(taskId, fromTaskList, toList)
    }
}
