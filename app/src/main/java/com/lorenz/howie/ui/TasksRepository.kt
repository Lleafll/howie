package com.lorenz.howie.ui

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.lorenz.howie.core.*
import com.lorenz.howie.database.*
import com.lorenz.howie.widget.HowieAppWidgetProvider
import kotlinx.coroutines.*
import java.time.LocalDate

class TasksRepository(
    private val _widgetUpdater: () -> Unit,
    private val _taskDao: TaskDao,
    private val _taskListDao: TaskListDao
) {
    private val _domainModel: Deferred<DomainModel> = GlobalScope.async {
        val taskLists =
            DatabaseModel(_taskDao.getAll(), _taskListDao.getAllTaskLists()).toDomainModel()
        DomainModel(taskLists)
    }

    suspend fun getTaskCounts(taskList: TaskListIndex?) =
        _domainModel.await().getTaskCounts(taskList)

    suspend fun getTask(taskIndex: TaskIndex) =
        _domainModel.await().getTask(taskIndex)

    suspend fun getTaskListName(taskList: TaskListIndex) =
        _domainModel.await().getTaskListName(taskList)

    suspend fun getTaskListNames() = _domainModel.await().getTaskListNames()

    suspend fun getTaskListInformation() = _domainModel.await().getTaskListInformation()

    suspend fun getUnarchivedTasks(taskList: TaskListIndex?, category: TaskCategory) =
        _domainModel.await().getUnarchivedTasks(taskList, category)

    suspend fun deleteTask(taskIndex: TaskIndex): Task {
        val task = _domainModel.await().deleteTask(taskIndex)
        saveAll()
        return task
    }

    suspend fun deleteTaskList(position: TaskListIndex): Boolean {
        val success = _domainModel.await().deleteTaskList(position)
        if (success) {
            saveAll()
        }
        return success
    }

    suspend fun moveTaskFromListToList(
        taskId: TaskIndex, toList: TaskListIndex
    ) {
        _domainModel.await().moveTaskFromListToList(taskId, toList)
        saveAll()
    }

    suspend fun renameTaskList(taskListId: TaskListIndex, newName: String) {
        _domainModel.await().renameTaskList(taskListId, newName)
        saveAll()
    }

    suspend fun doArchive(taskId: TaskIndex, date: LocalDate) {
        _domainModel.await().doArchive(taskId, date)
        saveAll()
    }

    suspend fun getArchive(taskList: TaskListIndex?): List<IndexedTask> {
        return _domainModel.await().getArchive(taskList)
    }

    suspend fun addTaskList(): TaskListIndex {
        val newIndex = _domainModel.await().addTaskList()
        saveAll()
        return newIndex
    }

    suspend fun snoozeToTomorrow(task: TaskIndex) {
        _domainModel.await().snoozeToTomorrow(task)
        saveAll()
    }

    suspend fun removeSnooze(task: TaskIndex): LocalDate? {
        val oldSnoozed = _domainModel.await().removeSnooze(task)
        saveAll()
        return oldSnoozed
    }

    private suspend fun saveAll() {
        withContext(Dispatchers.IO) {
            val databaseModel = _domainModel.await().taskLists.toDatabaseModel()
            _taskDao.deleteAll()
            _taskDao.insertAll(databaseModel.taskEntities)
            _taskListDao.deleteAll()
            _taskListDao.insertAll(databaseModel.taskListEntities)
        }
        _widgetUpdater()
    }

    suspend fun scheduleNext(task: TaskIndex) {
        _domainModel.await().scheduleNext(task)
        saveAll()
    }

    suspend fun unarchive(taskIndex: TaskIndex): LocalDate? {
        val oldArchivedate = _domainModel.await().unarchive(taskIndex)
        saveAll()
        return oldArchivedate
    }

    suspend fun addTask(taskList: TaskListIndex, task: Task): Boolean {
        val success = _domainModel.await().addTask(taskList, task)
        if (success) {
            saveAll()
        }
        return success
    }

    suspend fun updateTask(taskIndex: TaskIndex, task: Task): Boolean {
        val success = _domainModel.await().updateTask(taskIndex, task)
        if (success) {
            saveAll()
        }
        return success
    }

    suspend fun addSnooze(task: TaskIndex, snooze: LocalDate) {
        _domainModel.await().addSnooze(task, snooze)
        saveAll()
    }
}

fun buildDefaultWidgetUpdater(application: Application): () -> Unit {
    return {
        val appWidgetManager = AppWidgetManager.getInstance(application)
        HowieAppWidgetProvider().onUpdate(
            application, appWidgetManager, appWidgetManager.getAppWidgetIds(
                ComponentName(application, HowieAppWidgetProvider::class.java)
            )
        )
    }
}