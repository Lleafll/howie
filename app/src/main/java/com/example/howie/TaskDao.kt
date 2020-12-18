package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE importance = 0 AND due IS NOT NULL ORDER BY snoozed, due")
    fun getDoTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE importance = 0 AND due IS NULL ORDER BY snoozed")
    fun getDecideTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE importance = 1 AND due IS NOT NULL ORDER BY snoozed, due")
    fun getDelegateTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE importance = 1 AND due IS NULL ORDER BY snoozed")
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