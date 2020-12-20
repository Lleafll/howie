package com.example.howie

import androidx.annotation.WorkerThread

class TaskRepository(private val taskDao: TaskDao) {
    val tasks = taskDao.getAllTasks()
    val doTasks = taskDao.getDoTasks()
    val snoozedDoTasks = taskDao.getSnoozedDoTasks()
    val decideTasks = taskDao.getDecideTasks()
    val snoozedDecideTasks = taskDao.getSnoozedDecideTasks()
    val delegateTasks = taskDao.getDelegateTasks()
    val snoozedDelegateTasks = taskDao.getSnoozedDelegateTasks()
    val dropTasks = taskDao.getDropTasks()
    val snoozedDropTasks = taskDao.getSnoozedDropTasks()

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