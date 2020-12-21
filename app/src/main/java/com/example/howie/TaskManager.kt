package com.example.howie

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.Transformations.switchMap


class TaskManager(private val taskDao: TaskDao) : ViewModel() {
    private val taskListId = MutableLiveData<Int>()
    lateinit var doTasks: LiveData<List<Task>> private set
    lateinit var snoozedDoTasks: LiveData<List<Task>> private set
    lateinit var decideTasks: LiveData<List<Task>> private set
    lateinit var snoozedDecideTasks: LiveData<List<Task>> private set
    lateinit var delegateTasks: LiveData<List<Task>> private set
    lateinit var snoozedDelegateTasks: LiveData<List<Task>> private set
    lateinit var dropTasks: LiveData<List<Task>> private set
    lateinit var snoozedDropTasks: LiveData<List<Task>> private set
    lateinit var archive: LiveData<List<Task>> private set

    init {
        taskListId.value = 0
        setupAllTaskData()
    }

    private fun setupAllTaskData() {
        doTasks = switchMap(taskListId) { taskDao.getDoTasks() }
        snoozedDoTasks = switchMap(taskListId) { taskDao.getSnoozedDoTasks() }
        decideTasks = switchMap(taskListId) { taskDao.getDecideTasks() }
        snoozedDecideTasks = switchMap(taskListId) { taskDao.getSnoozedDecideTasks() }
        delegateTasks = switchMap(taskListId) { taskDao.getDelegateTasks() }
        snoozedDelegateTasks = switchMap(taskListId) { taskDao.getSnoozedDelegateTasks() }
        dropTasks = switchMap(taskListId) { taskDao.getDropTasks() }
        snoozedDropTasks = switchMap(taskListId) { taskDao.getSnoozedDropTasks() }
        archive = switchMap(taskListId) { taskDao.getArchive() }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun add(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun doArchive(id: Int) = viewModelScope.launch {
        taskDao.doArchive(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(id: Int) = viewModelScope.launch { taskDao.delete(id) }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun switchToTaskList(newTaskListId: Int) {
        taskListId.value = newTaskListId
    }

    companion object {
        private var instance: TaskManager? = null

        fun getInstance(applicationContext: Context): TaskManager {
            if (instance == null) {
                val database = TasksDatabaseSingleton.getDatabase(applicationContext)
                instance = TaskManager(database.getTaskDao())
            }
            return instance!!
        }
    }
}
