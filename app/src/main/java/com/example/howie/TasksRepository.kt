package com.example.howie

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.asLiveData

data class TaskCounts(
    val doCount: Int,
    val decideCount: Int,
    val delegateCount: Int,
    val dropCount: Int
)

private const val currentTaskListIdKey = "currentTaskListId"

class TasksRepository(
    private val taskDao: TaskDao,
    private val taskListDao: TaskListDao,
    private val preferences: SharedPreferences
) {
    val tasks = taskDao.getAllTasks().asLiveData()
    private val currentTaskListIdMutable = defaultTaskListId(preferences)
    val currentTaskListId: LiveData<Long> = currentTaskListIdMutable
    val doTasks = switchMap(currentTaskListId) { taskDao.getDoTasks(it).asLiveData() }
    val snoozedDoTasks =
        switchMap(currentTaskListId) { taskDao.getSnoozedDoTasks(it).asLiveData() }
    val decideTasks = switchMap(currentTaskListId) { taskDao.getDecideTasks(it).asLiveData() }
    val snoozedDecideTasks =
        switchMap(currentTaskListId) { taskDao.getSnoozedDecideTasks(it).asLiveData() }
    val delegateTasks = switchMap(currentTaskListId) { taskDao.getDelegateTasks(it).asLiveData() }
    val snoozedDelegateTasks =
        switchMap(currentTaskListId) { taskDao.getSnoozedDelegateTasks(it).asLiveData() }
    val dropTasks = switchMap(currentTaskListId) { taskDao.getDropTasks(it).asLiveData() }
    val snoozedDropTasks =
        switchMap(currentTaskListId) { taskDao.getSnoozedDropTasks(it).asLiveData() }
    val archive = switchMap(currentTaskListId) { taskDao.getArchive(it).asLiveData() }
    val taskLists = taskListDao.getAllTaskLists().asLiveData()
    val currentTaskList = switchMap(currentTaskListId) { taskListDao.getTaskList(it).asLiveData() }
    val lastInsertedTaskCategory = MutableLiveData<TaskCategory>()
    val countCurrentDoTasks = switchMap(currentTaskListId) { countDoTasks(it) }
    val countCurrentDecideTasks = switchMap(currentTaskListId) { countDecideTasks(it) }
    val countCurrentDelegateTasks = switchMap(currentTaskListId) { countDelegateTasks(it) }
    val countCurrentDropTasks = switchMap(currentTaskListId) { countDropTasks(it) }

    fun countDoTasks(taskListId: Long) = taskDao.countDoTasks(taskListId).asLiveData()
    fun countDecideTasks(taskListId: Long) = taskDao.countDecideTasks(taskListId).asLiveData()
    fun countDelegateTasks(taskListId: Long) = taskDao.countDelegateTasks(taskListId).asLiveData()
    fun countDropTasks(taskListId: Long) = taskDao.countDropTasks(taskListId).asLiveData()
    suspend fun add(task: Task) = taskDao.insert(task)
    suspend fun update(task: Task) = taskDao.update(task)
    suspend fun doArchive(id: Int) = taskDao.doArchive(id)
    suspend fun unarchive(id: Int) = taskDao.unarchive(id)
    suspend fun delete(id: Int) = taskDao.delete(id)
    fun getTask(id: Int) = taskDao.getTask(id).asLiveData()
    fun getTaskList(id: Long) = taskListDao.getTaskList(id).asLiveData()
    fun switchToTaskList(newTaskListId: Long) {
        currentTaskListIdMutable.value = newTaskListId
        lastInsertedTaskCategory.value = TaskCategory.DO
        with(preferences.edit()) {
            putLong(currentTaskListIdKey, newTaskListId)
            apply()
        }
    }

    suspend fun addTaskList(name: String) {
        val id = taskListDao.insert(TaskList(name))
        switchToTaskList(id)
    }

    suspend fun deleteTaskList(taskListId: Long) {
        if (taskListId == 0L) {
            return
        }
        switchToTaskList(0L)
        taskListDao.delete(taskListId)
        taskDao.deleteTaskListTasks(taskListId)
    }

    suspend fun moveToList(taskId: Int, taskListId: Long) =
        taskDao.moveToTaskList(taskId, taskListId)

    suspend fun renameTaskList(taskListId: Long, newName: String) {
        taskListDao.rename(taskListId, newName)
    }
}

private fun defaultTaskListId(preferences: SharedPreferences): MutableLiveData<Long> {
    val taskListId = MutableLiveData<Long>()
    taskListId.value = preferences.getLong(currentTaskListIdKey, 0)
    return taskListId
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