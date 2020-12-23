package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

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
private const val select = "SELECT *"
private const val count = "SELECT COUNT(*)"
private const val fromDoTasks =
    "FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order"
private const val fromDecideTasks =
    "FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order"
private const val fromDelegateTasks =
    "FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isNotSnoozed AND $isNotArchived $order"
private const val fromDropTasks =
    "FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived $order"

@Dao
interface TaskDao {
    @Query("$select FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("$select $fromDoTasks")
    fun getDoTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDoTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select $fromDecideTasks")
    fun getDecideTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDecideTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select $fromDelegateTasks")
    fun getDelegateTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDelegateTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select $fromDropTasks")
    fun getDropTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isSnoozed AND $isNotArchived $order")
    fun getSnoozedDropTasks(taskListId: Long): LiveData<List<Task>>

    @Query("$select FROM task WHERE id = :id")
    fun getTask(id: Int): LiveData<Task>

    @Query("$select FROM task WHERE $isOnList AND $isArchived ORDER BY archived DESC")
    fun getArchive(taskListId: Long): LiveData<List<Task>>

    @Query("$count $fromDoTasks")
    fun countDoTasks(taskListId: Long): Flow<Int>

    @Query("$count $fromDecideTasks")
    fun countDecideTasks(taskListId: Long): Flow<Int>

    @Query("$count $fromDelegateTasks")
    fun countDelegateTasks(taskListId: Long): Flow<Int>

    @Query("$count $fromDropTasks")
    fun countDropTasks(taskListId: Long): Flow<Int>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("UPDATE task SET archived = $today WHERE id = :id")
    suspend fun doArchive(id: Int)

    @Query("UPDATE task SET taskListId = :taskListId WHERE id = :id")
    suspend fun moveToTaskList(id: Int, taskListId: Long)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM task WHERE taskListId = :taskListId")
    suspend fun deleteTaskListTasks(taskListId: Long)
}