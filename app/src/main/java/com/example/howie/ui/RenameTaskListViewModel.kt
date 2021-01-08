package com.example.howie.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.howie.database.getDatabase
import kotlinx.coroutines.launch

class RenameTaskListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    fun renameTaskList(taskListId: Int, newName: String) = viewModelScope.launch {
        repository.renameTaskList(taskListId, newName)
    }
}