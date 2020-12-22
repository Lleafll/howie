package com.example.howie

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface TaskListDao {
    @Query("SELECT * FROM TaskList ORDER BY name")
    fun getAllTaskLists(): LiveData<List<TaskList>>
}