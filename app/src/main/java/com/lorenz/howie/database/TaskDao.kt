package com.lorenz.howie.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.howie.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAll(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("DELETE FROM Task")
    suspend fun deleteAll()
}