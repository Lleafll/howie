package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskListDao {
    @Query("SELECT * FROM TaskList ORDER BY name")
    fun getAllTaskLists(): LiveData<List<TaskList>>

    @Query("SELECT * FROM TaskList WHERE id = :id")
    fun getTaskList(id: Long): LiveData<TaskList>

    @Insert
    suspend fun insert(taskList: TaskList): Long

    @Query("UPDATE TaskList SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("DELETE FROM TaskList WHERE id = :id")
    suspend fun delete(id: Long)
}