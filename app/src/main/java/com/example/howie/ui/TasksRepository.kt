package com.example.howie.ui

import com.example.howie.core.DomainModel
import com.example.howie.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksRepository(private val _taskDao: TaskDao, private val _taskListDao: TaskListDao) {
    private val _domainModel: DomainModel

    init {
        val taskLists =
            DatabaseModel(_taskDao.getAll(), _taskListDao.getAllTaskLists()).toDomainModel()
        _domainModel = DomainModel(taskLists)
    }

    fun getTaskCounts(taskList: Int) = _domainModel.getTaskCounts(taskList)

    fun getArchive =

    suspend fun deleteTaskList(position: Int) {
        if (_domainModel.deleteTaskList(position)) {
            saveAll()
        }
    }

    private suspend fun saveAll() {
        withContext(Dispatchers.IO) {
            val databaseModel = _domainModel.taskLists.toDatabaseModel()
            _taskDao.insertAll(databaseModel.taskEntities)
            _taskListDao.insertAll(databaseModel.taskListEntities)
        }
    }
}
