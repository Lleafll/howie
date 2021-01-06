package com.example.howie

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
    }

    val currentTaskListId = repository.currentTaskListId

    fun getTask(id: Int) = repository.getTask(id)

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun add(task: Task) = viewModelScope.launch {
        repository.add(task)
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        repository.doArchive(id)
    }

    fun unarchive(id: Int) = viewModelScope.launch {
        repository.unarchive(id)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }
}