package com.example.howie

import androidx.annotation.WorkerThread

class TaskRepository(private val taskDao: TaskDao) {
    val tasks = taskDao.getAllTasks()
    val doTasks = taskDao.getDoTasks()
    val decideTasks = taskDao.getDecideTasks()
    val delegateTasks = taskDao.getDelegateTasks()
    val dropTasks = taskDao.getDropTasks()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(id: Int) {
        taskDao.delete(id)
    }

    fun getTask(id: Int) = taskDao.getTask(id)
}