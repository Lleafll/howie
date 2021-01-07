package com.example.howie.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    val name: String,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)