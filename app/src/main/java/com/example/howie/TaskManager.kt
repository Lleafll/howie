package com.example.howie

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TaskManager(
    private val taskDao: TaskDao, private val taskListDao: TaskListDao
) : ViewModel() {
    var currentTaskListId = 0L
        private set
    private val taskListIdFlow = defaultTaskListId(currentTaskListId)
    val tasks: Flow<List<Task>> = taskDao.getAllTasks()
    val doTasks = toFlowWhichIsUpdatedOnChange { taskDao.getDoTasks(it) }
    val snoozedDoTasks = toFlowWhichIsUpdatedOnChange { taskDao.getSnoozedDoTasks(it) }
    val decideTasks = toFlowWhichIsUpdatedOnChange { taskDao.getDecideTasks(it) }
    val snoozedDecideTasks = toFlowWhichIsUpdatedOnChange { taskDao.getSnoozedDecideTasks(it) }
    val delegateTasks = toFlowWhichIsUpdatedOnChange { taskDao.getDelegateTasks(it) }
    val snoozedDelegateTasks = toFlowWhichIsUpdatedOnChange { taskDao.getSnoozedDelegateTasks(it) }
    val dropTasks = toFlowWhichIsUpdatedOnChange { taskDao.getDropTasks(it) }
    val snoozedDropTasks = toFlowWhichIsUpdatedOnChange { taskDao.getSnoozedDropTasks(it) }
    val archive = toFlowWhichIsUpdatedOnChange { taskDao.getArchive(it) }
    val taskLists = taskListDao.getAllTaskLists()
    val currentTaskList = toFlowWhichIsUpdatedOnChange { taskListDao.getTaskList(it) }
    val lastInsertedTaskCategory = MutableStateFlow(TaskCategory.DO)

    private fun <T> toFlowWhichIsUpdatedOnChange(func: (Long) -> Flow<T>): Flow<T> {
        return flow {
            taskListIdFlow.collect { emit(func(it).first()) }
        }
    }

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

    fun delete(id: Int) = viewModelScope.launch { taskDao.delete(id) }

    fun getTask(id: Int) = taskDao.getTask(id)

    fun getTaskList(id: Long) = taskListDao.getTaskListFlow(id)

    fun switchToTaskList(newTaskListId: Long) {
        currentTaskListId = newTaskListId
        lastInsertedTaskCategory.value = TaskCategory.DO
        taskListIdFlow.value = newTaskListId
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

    fun getTaskCounts(taskListId: Long): Flow<List<Int>> {
        val taskCounts = MutableStateFlow<List<Int>>(listOf())
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

        fun getInstance(applicationContext: Context): TaskManager {
            if (instance == null) {
                val database = TasksDatabaseSingleton.getDatabase(applicationContext)
                instance = TaskManager(database.getTaskDao(), database.getTaskListDao())
            }
            return instance!!
        }
    }
}

@ExperimentalCoroutinesApi
private fun defaultTaskListId(value: Long): MutableStateFlow<Long> {
    return MutableStateFlow(value)
}
