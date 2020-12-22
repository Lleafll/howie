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
private const val isOnList = "(taskListId = :taskListId)"

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDoTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDoTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDecideTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDecideTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDelegateTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDelegateTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order")
    fun getDropTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDropTasks(taskListId: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): LiveData<Task>

    @Query("SELECT * FROM task WHERE $isOnList AND $isArchived ORDER BY archived DESC")
    fun getArchive(taskListId: Int): LiveData<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("UPDATE task SET ARCHIVED = $today WHERE id = :id")
    suspend fun doArchive(id: Int)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM task WHERE taskListId = :taskListId")
    suspend fun deleteTaskListTasks(taskListId: Int)
}