package com.example.howie.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskListDao {
    @Query("SELECT * FROM TaskList")
    fun getAllTaskLists(): List<TaskListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taskListEntities: List<TaskListEntity>)
}