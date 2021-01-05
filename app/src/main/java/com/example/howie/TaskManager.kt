package com.example.howie

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.switchMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class TaskManager(application: Application) : AndroidViewModel(application) {
    private val taskDao: TaskDao
    private val taskListDao: TaskListDao

    init {
        val database = TasksDatabaseSingleton.getDatabase(application.applicationContext)
        taskDao = database.getTaskDao()
        taskListDao = database.getTaskListDao()
    }

    var currentTaskListId = 0L
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
    val currentTaskList: LiveData<TaskList> =
        switchMap(taskListIdLiveData) { taskListDao.getTaskList(it) }
    val lastInsertedTaskCategory = MutableLiveData<TaskCategory>()
    val countCurrentDoTasks = switchMap(taskListIdLiveData) { countDoTasks(it).asLiveData() }
    val countCurrentDecideTasks =
        switchMap(taskListIdLiveData) { countDecideTasks(it).asLiveData() }
    val countCurrentDelegateTasks =
        switchMap(taskListIdLiveData) { countDelegateTasks(it).asLiveData() }
    val countCurrentDropTasks = switchMap(taskListIdLiveData) { countDropTasks(it).asLiveData() }

    fun add(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        lastInsertedTaskCategory.value = taskCategory(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        taskDao.update(task)
    }

    fun doArchive(id: Int) = viewModelScope.launch {
        taskDao.doArchive(id)
    }

    fun unarchive(id: Int) = viewModelScope.launch {
        taskDao.unarchive(id)
    }

    fun delete(id: Int) = viewModelScope.launch { taskDao.delete(id) }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun getTaskList(id: Long) = taskListDao.getTaskListFlow(id)

    fun switchToTaskList(newTaskListId: Long) {
        currentTaskListId = newTaskListId
        lastInsertedTaskCategory.value = TaskCategory.DO
        taskListIdLiveData.value = newTaskListId
    }

    fun addTaskList(name: String) = viewModelScope.launch {
        val id = taskListDao.insert(TaskList(name))
        switchToTaskList(id)
    }

    fun deleteCurrentTaskList() = viewModelScope.launch {
        if (currentTaskListId != 0L) {
            val taskListId = currentTaskListId
            switchToTaskList(0L)
            taskListDao.delete(taskListId)
            taskDao.deleteTaskListTasks(taskListId)
        }
    }

    fun renameCurrentTaskList(newName: String) = viewModelScope.launch {
        taskListDao.rename(currentTaskListId, newName)
    }

    fun getTaskCounts(taskListId: Long): LiveData<List<Int>> {
        val taskCounts = MutableLiveData<List<Int>>()
        viewModelScope.launch {
            val values: Flow<Int> = flow {
                emit(countDoTasks(taskListId).first())
                emit(countDecideTasks(taskListId).first())
                emit(countDelegateTasks(taskListId).first())
                emit(countDropTasks(taskListId).first())
            }
            taskCounts.value = values.toList()
        }
        return taskCounts
    }

    fun moveToList(taskId: Int, taskListId: Long) = viewModelScope.launch {
        taskDao.moveToTaskList(taskId, taskListId)
    }

    fun countDoTasks(taskListId: Long) = taskDao.countDoTasks(taskListId)

    fun countDecideTasks(taskListId: Long) = taskDao.countDecideTasks(taskListId)

    fun countDelegateTasks(taskListId: Long) = taskDao.countDelegateTasks(taskListId)

    fun countDropTasks(taskListId: Long) = taskDao.countDropTasks(taskListId)

    companion object {
        private var instance: TaskManager? = null

        fun getInstance(application: Application): TaskManager {
            if (instance == null) {
                instance = TaskManager(application)
            }
            return instance!!
        }

        fun getInstance() = instance!!
    }
}

private fun defaultTaskListId(value: Long): MutableLiveData<Long> {
    val taskListId = MutableLiveData<Long>()
    taskListId.value = value
    return taskListId
}
