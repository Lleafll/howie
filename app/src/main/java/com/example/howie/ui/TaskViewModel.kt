package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.howie.core.Task
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository
    var taskList by Delegates.notNull<Int>()

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    fun getTask(task: Int) = liveData {
        emit(repository.getTask(taskList, task))
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        // TODO: Implement
    }

    fun addTask(task: Task) = viewModelScope.launch {
        // TODO: Implement
    }

    fun doArchive(task: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun unarchive(task: Int) = viewModelScope.launch {
        // TODO: Implement
    }

    fun deleteTask(task: Int) = viewModelScope.launch {
        // TODO: Implement
    }
}