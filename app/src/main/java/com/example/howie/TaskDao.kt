package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAll(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): LiveData<Task>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}