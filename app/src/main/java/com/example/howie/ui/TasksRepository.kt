package com.example.howie.ui

import com.example.howie.core.DomainModel
import com.example.howie.core.TaskCategory
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

    fun getTask(taskListIndex: Int, taskIndex: Int) = _domainModel.getTask(taskListIndex, taskIndex)

    fun getTaskListNames() = _domainModel.getTaskListNames()

    fun getTaskListInformation(taskList: Int) = _domainModel.getTaskListInformation(taskList)

    fun getUnarchivedTasks(taskList: Int, category: TaskCategory) =
        _domainModel.getUnarchivedTasks(taskList, category)

    suspend fun deleteTaskList(position: Int) {
        if (_domainModel.deleteTaskList(position)) {
            saveAll()
        }
    }

    suspend fun moveTaskFromListToList(taskId: Int, fromTaskList: Int, toList: Int) {
        if (_domainModel.moveTaskFromListToList(taskId, fromTaskList, toList)) {
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
            val databaseModel = _domainModel.taskLists.toDatabaseModel()
            _taskDao.insertAll(databaseModel.taskEntities)
            _taskListDao.insertAll(databaseModel.taskListEntities)
        }
    }
}
