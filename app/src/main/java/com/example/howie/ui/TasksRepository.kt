package com.example.howie.ui

import com.example.howie.core.*
import com.example.howie.database.*
import kotlinx.coroutines.*

class TasksRepository(private val _taskDao: TaskDao, private val _taskListDao: TaskListDao) {
    private val _domainModel: Deferred<DomainModel>

    init {
        _domainModel = GlobalScope.async {
            val taskLists =
                DatabaseModel(_taskDao.getAll(), _taskListDao.getAllTaskLists()).toDomainModel()
            DomainModel(taskLists)
        }
    }

    suspend fun getTaskCounts(taskList: TaskListIndex) =
        _domainModel.await().getTaskCounts(taskList)

    suspend fun getTask(taskListIndex: TaskListIndex, taskIndex: TaskIndex) =
        _domainModel.await().getTask(taskListIndex, taskIndex)

    suspend fun getTaskListName(taskList: TaskListIndex) =
        _domainModel.await().getTaskListName(taskList)

    suspend fun getTaskListNames() = _domainModel.await().getTaskListNames()

    suspend fun getTaskListInformation(taskList: Int) =
        _domainModel.await().getTaskListInformation(taskList)

    suspend fun getTaskListInformation() = _domainModel.await().getTaskListInformation()

    suspend fun getUnarchivedTasks(taskList: TaskListIndex, category: TaskCategory) =
        _domainModel.await().getUnarchivedTasks(taskList, category)

    suspend fun deleteTaskList(position: TaskListIndex): Boolean {
        val success = _domainModel.await().deleteTaskList(position)
        if (success) {
            saveAll()
        }
        return success
    }

    suspend fun moveTaskFromListToList(
        taskId: TaskIndex,
        fromTaskList: TaskListIndex,
        toList: TaskListIndex
    ) {
        if (_domainModel.await().moveTaskFromListToList(taskId, fromTaskList, toList)) {
            saveAll()
        }
    }

    suspend fun renameTaskList(taskListId: TaskListIndex, newName: String) {
        TODO("Implement")
    }

    suspend fun doArchive(taskListId: TaskListIndex, taskId: TaskIndex) {
        _domainModel.await().doArchive(taskListId, taskId)
        saveAll()
    }

    suspend fun getArchive(taskList: TaskListIndex): List<IndexedTask> {
        return _domainModel.await().getArchive(taskList)
    }

    suspend fun addTaskList(): TaskListIndex {
        val newIndex = _domainModel.await().addTaskList()
        saveAll()
        return newIndex
    }

    suspend fun snoozeToTomorrow(taskList: TaskListIndex, task: TaskIndex) {
        _domainModel.await().snoozeToTomorrow(taskList, task)
        saveAll()
    }

    suspend fun removeSnooze(taskList: TaskListIndex, task: TaskIndex) {
        _domainModel.await().removeSnooze(taskList, task)
        saveAll()
    }

    private suspend fun saveAll() {
        withContext(Dispatchers.IO) {
            val databaseModel = _domainModel.await().taskLists.toDatabaseModel()
            /* TODO: Implement
            _taskDao.insertAll(databaseModel.taskEntities)
            _taskListDao.insertAll(databaseModel.taskListEntities)
             */
        }
    }

    suspend fun scheduleNext(taskList: TaskListIndex, task: TaskIndex) {
        _domainModel.await().scheduleNext(taskList, task)
        saveAll()
    }
}
