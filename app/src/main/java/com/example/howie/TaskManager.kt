package com.example.howie

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskManager(application: Application) : AndroidViewModel(application) {
    private val repository: TasksRepository

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        repository = TasksRepository(database.getTaskDao(), database.getTaskListDao())
    }

    val tasks = repository.tasks
    val doTasks = repository.doTasks
    val snoozedDoTasks = repository.snoozedDoTasks
    val decideTasks = repository.decideTasks
    val snoozedDecideTasks = repository.snoozedDecideTasks
    val delegateTasks = repository.delegateTasks
    val snoozedDelegateTasks = repository.snoozedDelegateTasks
    val dropTasks = repository.dropTasks
    val snoozedDropTasks = repository.snoozedDropTasks
    val archive = repository.archive
    val taskLists = repository.taskLists
    val currentTaskList = repository.currentTaskList
    val currentTaskListId = repository.currentTaskListId
    val lastInsertedTaskCategory = repository.lastInsertedTaskCategory
    val countCurrentDoTasks = repository.countCurrentDoTasks
    val countCurrentDecideTasks = repository.countCurrentDecideTasks
    val countCurrentDelegateTasks = repository.countCurrentDelegateTasks
    val countCurrentDropTasks = repository.countCurrentDropTasks

    fun add(task: Task) = viewModelScope.launch {
        repository.add(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
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

    fun getTask(id: Int) = repository.getTask(id)

    fun getTaskList(id: Long) = repository.getTaskList(id)

    fun switchToTaskList(newTaskListId: Long) = repository.switchToTaskList(newTaskListId)

    fun addTaskList(name: String) = viewModelScope.launch {
        repository.addTaskList(name)
    }

    fun deleteCurrentTaskList() = viewModelScope.launch {
        repository.deleteCurrentTaskList()
    }

    fun renameCurrentTaskList(newName: String) = viewModelScope.launch {
        repository.renameCurrentTaskList(newName)
    }

    fun getTaskCounts(taskListId: Long) = repository.getTaskCounts(taskListId)

    fun moveToList(taskId: Int, taskListId: Long) = viewModelScope.launch {
        repository.moveToList(taskId, taskListId)
    }

    companion object {
        private var instance: TaskManager? = null

        @Deprecated("Use a proper ViewModel")
        fun getInstance(application: Application): TaskManager {
            if (instance == null) {
                instance = TaskManager(application)
            }
            return instance!!
        }

        @Deprecated("Use a proper ViewModel")
        fun getInstance() = instance!!
    }
}
