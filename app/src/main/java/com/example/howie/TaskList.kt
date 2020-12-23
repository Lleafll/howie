package com.example.howie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskList(
    val name: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}