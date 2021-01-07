package com.example.howie.ui

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.asLiveData
import com.example.howie.Task
import com.example.howie.database.TaskList
import com.example.howie.database.TaskListDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TaskCounts(
    val doCount: Int,
    val decideCount: Int,
    val delegateCount: Int,
    val dropCount: Int
)

private const val currentTaskListIdKey = "currentTaskListId"


class TasksRepository(
    private val _taskDao: TaskDao,
    private val _taskListDao: TaskListDao,
    private val _preferences: SharedPreferences
) {
    private val _domainModel = DomainModel(
        _taskDao.getAllTasks(),
        _taskListDao.getAllTaskLists(),
        _preferences.getLong(currentTaskListIdKey, 0)
    )

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> by this::_tasks
    private val _currentTaskListId = MutableLiveData<Long>()
    val currentTaskListId: LiveData<Long> by this::_currentTaskListId
    private val _currentTasks = MutableLiveData<CategorizedTasks>()
    val currentTasks: LiveData<CategorizedTasks> by this::_currentTasks
    private val _currentArchivedTasks = MutableLiveData<List<Task>>()
    val currentArchivedTasks: LiveData<List<Task>> by this::_currentArchivedTasks
    private val _taskLists = MutableLiveData<List<TaskList>>()
    val taskLists = _taskListDao.getAllTaskLists().asLiveData()
    val currentTaskList = switchMap(currentTaskListId) { _taskListDao.getTaskList(it).asLiveData() }
    val lastInsertedTaskCategory = MutableLiveData<TaskCategory>()

    fun countDoTasks(taskListId: Long) = _taskDao.countDoTasks(taskListId).asLiveData()
    fun countDecideTasks(taskListId: Long) = _taskDao.countDecideTasks(taskListId).asLiveData()
    fun countDelegateTasks(taskListId: Long) = _taskDao.countDelegateTasks(taskListId).asLiveData()
    fun countDropTasks(taskListId: Long) = _taskDao.countDropTasks(taskListId).asLiveData()
    suspend fun add(task: Task) = _taskDao.insert(task)
    suspend fun update(task: Task) = _taskDao.update(task)
    suspend fun doArchive(id: Int) = _taskDao.doArchive(id)
    suspend fun unarchive(id: Int) = _taskDao.unarchive(id)
    suspend fun delete(id: Int) = _taskDao.delete(id)
    fun getTask(id: Int) = _taskDao.getTask(id).asLiveData()
    fun getTaskList(id: Long) = _taskListDao.getTaskList(id).asLiveData()
    fun switchToTaskList(newTaskListId: Long) {
        _currentTaskListId.value = newTaskListId
        lastInsertedTaskCategory.value = TaskCategory.DO
        with(_preferences.edit()) {
            putLong(currentTaskListIdKey, newTaskListId)
            apply()
        }
    }

    suspend fun addTaskList(name: String) {
        val id = _taskListDao.insert(TaskList(name))
        switchToTaskList(id)
    }

    suspend fun deleteTaskList(taskListId: Long) {
        if (_domainModel.deleteTaskList(taskListId)) {
            saveAll()
        }
    }

    suspend fun moveToList(taskId: Int, taskListId: Long) =
        _taskDao.moveToTaskList(taskId, taskListId)

    suspend fun renameTaskList(taskListId: Long, newName: String) {
        _taskListDao.rename(taskListId, newName)
    }

    private suspend fun saveAll() {
        saveTasks()
        saveTaskLists()
        saveCurrentTaskListId()
    }

    private suspend fun saveTasks() {
        withContext(Dispatchers.IO) {
            _taskDao.insertAll(_domainModel.tasks)
        }
    }

    private suspend fun saveTaskLists() {
        withContext(Dispatchers.IO) {
            _taskListDao.insertAll(_domainModel.taskLists)
        }
    }

    private fun saveCurrentTaskListId() {
        with(_preferences.edit()) {
            putLong(currentTaskListIdKey, _domainModel.currentTaskListId)
            apply()
        }
    }
}

fun TasksRepository.getTaskCounts(taskListId: Long): LiveData<TaskCounts> {
    val taskCounts = MediatorLiveData<TaskCounts>()
    var doCount: Int? = null
    var decideCount: Int? = null
    var delegateCount: Int? = null
    var dropCount: Int? = null
    val assignCounts = {
        if (doCount != null && decideCount != null && delegateCount != null && dropCount != null) {
            taskCounts.value =
                TaskCounts(doCount!!, decideCount!!, delegateCount!!, dropCount!!)
        }
    }
    taskCounts.addSource(countDoTasks(taskListId)) {
        doCount = it
        assignCounts()
    }
    taskCounts.addSource(countDecideTasks(taskListId)) {
        decideCount = it
        assignCounts()
    }
    taskCounts.addSource(countDelegateTasks(taskListId)) {
        delegateCount = it
        assignCounts()
    }
    taskCounts.addSource(countDropTasks(taskListId)) {
        dropCount = it
        assignCounts()
    }
    return taskCounts
}

fun List<TaskList>.as