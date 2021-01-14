package com.example.howie.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskList")
data class TaskListEntity(
    val name: String,
    @PrimaryKey
    var id: Long
)