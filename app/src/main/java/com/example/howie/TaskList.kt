package com.example.howie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val name: String
)