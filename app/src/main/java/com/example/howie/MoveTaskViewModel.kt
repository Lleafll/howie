package com.example.howie

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoveTaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
    }

    val taskLists = repository.taskLists

    fun moveToList(taskId: Int, taskListId: Long) = viewModelScope.launch {
        repository.moveToList(taskId, taskListId)
    }
}