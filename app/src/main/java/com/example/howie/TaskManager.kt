package com.example.howie

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

const val HOWIE_SHARED_PREFERENCES_KEY = "howie_default_shared_preferences"

class TaskManager(application: Application, private val repository: TasksRepository) :
    AndroidViewModel(application) {

    constructor(application: Application) : this(application, {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        val preferences =
            application.getSharedPreferences(HOWIE_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        TasksRepository(database.getTaskDao(), database.getTaskListDao(), preferences)
    }())

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

    fun switchToTaskList(newTaskListId: Long) = repository.switchToTaskList(newTaskListId)

    fun addTaskList(name: String) = viewModelScope.launch {
        repository.addTaskList(name)
    }

    fun deleteTaskList(taskListId: Long) = viewModelScope.launch {
        repository.deleteTaskList(taskListId)
    }

    fun renameTaskList(taskListId: Long, newName: String) = viewModelScope.launch {
        repository.renameTaskList(taskListId, newName)
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
    }
}
