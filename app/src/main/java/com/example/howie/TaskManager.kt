package com.example.howie

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class TaskManager(private val repository: TaskRepository) : ViewModel() {
    val tasks: LiveData<List<Task>> = repository.tasks
    val doTasks = repository.doTasks
    val decideTasks = repository.decideTasks
    val delegateTasks = repository.delegateTasks
    val dropTasks = repository.dropTasks

    fun add(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(id: Int) = viewModelScope.launch {
        repository.delete(id)
    }

    fun getTask(id: Int) = repository.getTask(id)

    companion object {
        private var instance: TaskManager? = null

        fun getInstance(applicationContext: Context) : TaskManager {
            if (instance == null) {
                val database  = TasksDatabaseSingleton.getDatabase(applicationContext)
                val repository = TaskRepository(database.getTaskDao())
                instance = TaskManager(repository)
            }
            return instance!!
        }
    }
}