package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.*

private const val isImportant = "importance = 0"
private const val isUnimportant = "importance = 0"
private const val isDue = "due IS NOT NULL"
private const val isNotDue = "due IS NULL"
private const val isSnoozed = "snoozed IS NOT NULL AND snoozed > due"
private const val isNotSnoozed = "snoozed IS NULL OR snoozed <= due"
private const val order = "ORDER BY snoozed, due"

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isDue AND $isNotSnoozed $order")
    fun getDoTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isDue AND $isSnoozed $order")
    fun getSnoozedDoTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isNotDue $order")
    fun getDecideTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isDue $order")
    fun getDelegateTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isNotDue $order")
    fun getDropTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): LiveData<Task>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)
}