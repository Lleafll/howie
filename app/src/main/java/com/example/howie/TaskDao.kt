package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.*

private const val isImportant = "(importance = 0)"
private const val isUnimportant = "(importance = 1)"
private const val isDue = "(due IS NOT NULL)"
private const val isNotDue = "(due IS NULL)"
private const val numberOfSecondsInADay = 86400
private const val today = "(STRFTIME('%s','now') / $numberOfSecondsInADay)"
private const val isSnoozed = "(snoozed IS NOT NULL AND (snoozed > $today))"
private const val isNotSnoozed = "(snoozed IS NULL OR (snoozed <= $today))"
private const val order = "ORDER BY snoozed, due"
private const val isArchived = "(archived IS NOT NULL)"
private const val isNotArchived = "(archived IS NULL)"

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDoTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDoTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDecideTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isImportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDecideTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDelegateTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDelegateTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDropTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isUnimportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDropTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): LiveData<Task>

    @Query("SELECT * FROM task WHERE $isArchived ORDER BY archived DESC")
    fun getArchive(): LiveData<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("UPDATE task SET ARCHIVED = $today WHERE id = :id")
    suspend fun doArchive(id: Int)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)
}