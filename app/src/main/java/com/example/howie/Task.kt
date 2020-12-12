package com.example.howie

import java.util.*

enum class Importance {
    IMPORTANT, UNIMPORTANT
}

data class Task(
    val name: String,
    val importance: Importance,
    val due: Date,
    val snoozed: Date,
    val completed: Date?
)