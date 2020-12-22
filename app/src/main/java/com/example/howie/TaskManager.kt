package com.example.howie

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.Transformations.switchMap


class TaskManager(
    private val taskDao: TaskDao, private val taskListDao: TaskListDao
) : ViewModel() {
    var currentTaskListId = 0
        private set
    private val taskListIdLiveData = defaultTaskListId(currentTaskListId)
    val tasks: LiveData<List<Task>> = taskDao.getAllTasks()
    val doTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getDoTasks(it) }
    val snoozedDoTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getSnoozedDoTasks(it) }
    val decideTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getDecideTasks(it) }
    val snoozedDecideTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getSnoozedDecideTasks(it) }
    val delegateTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getDelegateTasks(it) }
    val snoozedDelegateTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getSnoozedDelegateTasks(it) }
    val dropTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getDropTasks(it) }
    val snoozedDropTasks: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getSnoozedDropTasks(it) }
    val archive: LiveData<List<Task>> =
        switchMap(taskListIdLiveData) { taskDao.getArchive(it) }
    val taskLists = taskListDao.getAllTaskLists()

    fun add(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task)
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        taskDao.doArchive(id)
    }

    fun delete(id: Int) = viewModelScope.launch { taskDao.delete(id) }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun switchToTaskList(newTaskListId: Int) {
        currentTaskListId = newTaskListId
        taskListIdLiveData.value = newTaskListId
    }

    fun addTaskList(name: String) = viewModelScope.launch {
        taskListDao.insert(TaskList(name))
    }

    companion object {
        private var instance: TaskManager? = null

        fun getInstance(applicationContext: Context): TaskManager {
            if (instance == null) {
                val database = TasksDatabaseSingleton.getDatabase(applicationContext)
                instance = TaskManager(database.getTaskDao(), database.getTaskListDao())
            }
            return instance!!
        }
    }
}

private fun defaultTaskListId(value: Int): MutableLiveData<Int> {
    val taskListId = MutableLiveData<Int>()
    taskListId.value = value
    return taskListId
}