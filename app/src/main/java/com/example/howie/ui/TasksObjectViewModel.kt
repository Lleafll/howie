package com.example.howie.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class TasksObjectViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
    }
    
    val doTasks = repository.doTasks
    val snoozedDoTasks = repository.snoozedDoTasks
    val decideTasks = repository.decideTasks
    val snoozedDecideTasks = repository.snoozedDecideTasks
    val delegateTasks = repository.delegateTasks
    val snoozedDelegateTasks = repository.snoozedDelegateTasks
    val dropTasks = repository.dropTasks
    val snoozedDropTasks = repository.snoozedDropTasks
}