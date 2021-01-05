package com.example.howie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

private const val isImportant = "(importance = 0)"
private const val isUnimportant = "(importance = 1)"
private const val isDue = "(due IS NOT NULL)"
private const val isNotDue = "(due IS NULL)"
private const val numberOfSecondsInADay = 86400
private const val today = "(STRFTIME('%s','now') / $numberOfSecondsInADay)"
private const val isSnoozed = "(snoozed IS NOT NULL AND (snoozed > $today))"
private const val isNotSnoozed = "(snoozed IS NULL OR (snoozed <= $today))"
private const val orderByDue = "ORDER BY due"
private const val orderBySnoozed = "ORDER BY snoozed"
private const val orderByArchived = "ORDER BY archived DESC"
private const val isArchived = "(archived IS NOT NULL)"
private const val isNotArchived = "(archived IS NULL)"
private const val isOnList = "(taskListId = :taskListId)"
private const val select = "SELECT *"
private const val count = "SELECT COUNT(*)"
private const val fromDoTasks =
    "FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isNotSnoozed AND $isNotArchived"
private const val fromDecideTasks =
    "FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived"
private const val fromDelegateTasks =
    "FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isNotSnoozed AND $isNotArchived"
private const val fromDropTasks =
    "FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isNotSnoozed AND $isNotArchived"

@Dao
interface TaskDao {
    @Query("$select FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Query("$select $fromDoTasks $orderByDue")
    fun getDoTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isImportant AND $isDue AND $isSnoozed AND $isNotArchived $orderBySnoozed")
    fun getSnoozedDoTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select $fromDecideTasks $orderByDue")
    fun getDecideTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isImportant AND $isNotDue AND $isSnoozed AND $isNotArchived $orderBySnoozed")
    fun getSnoozedDecideTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select $fromDelegateTasks $orderByDue")
    fun getDelegateTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isUnimportant AND $isDue AND $isSnoozed AND $isNotArchived $orderBySnoozed")
    fun getSnoozedDelegateTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select $fromDropTasks $orderByDue")
    fun getDropTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select FROM task WHERE $isOnList AND $isUnimportant AND $isNotDue AND $isSnoozed AND $isNotArchived $orderBySnoozed")
    fun getSnoozedDropTasks(taskListId: Long): Flow<List<Task>>

    @Query("$select FROM task WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("$select FROM task WHERE $isOnList AND $isArchived $orderByArchived")
    fun getArchive(taskListId: Long): Flow<List<Task>>

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

    @Query("UPDATE task SET archived = NULL WHERE id = :id")
    suspend fun unarchive(id: Int)

    @Query("UPDATE task SET taskListId = :taskListId WHERE id = :id")
    suspend fun moveToTaskList(id: Int, taskListId: Long)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM task WHERE taskListId = :taskListId")
    suspend fun deleteTaskListTasks(taskListId: Long)
}