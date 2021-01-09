package com.example.howie.ui

import com.example.howie.core.DomainModel
import com.example.howie.core.TaskCategory
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

    suspend fun getTaskCounts(taskList: Int) = _domainModel.await().getTaskCounts(taskList)

    suspend fun getTask(taskListIndex: Int, taskIndex: Int) =
        _domainModel.await().getTask(taskListIndex, taskIndex)

    suspend fun getTaskListName(taskList: Int) = _domainModel.await().getTaskListName(taskList)

    suspend fun getTaskListNames() = _domainModel.await().getTaskListNames()

    suspend fun getTaskListInformation(taskList: Int) =
        _domainModel.await().getTaskListInformation(taskList)

    suspend fun getTaskListInformation() = _domainModel.await().getTaskListInformation()

    suspend fun getUnarchivedTasks(taskList: Int, category: TaskCategory) =
        _domainModel.await().getUnarchivedTasks(taskList, category)

    suspend fun deleteTaskList(position: Int) {
        if (_domainModel.await().deleteTaskList(position)) {
            saveAll()
        }
    }

    suspend fun moveTaskFromListToList(taskId: Int, fromTaskList: Int, toList: Int) {
        if (_domainModel.await().moveTaskFromListToList(taskId, fromTaskList, toList)) {
            saveAll()
        }
    }

    suspend fun renameTaskList(taskListId: Int, newName: String) {
        TODO("Implement")
    }

    suspend fun doArchive(taskListId: Int, taskId: Int) {
        TODO("Implement")
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
}
