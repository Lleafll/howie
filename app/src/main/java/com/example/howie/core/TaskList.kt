package com.example.howie.core

data class TaskList (
    val name: String,
    val tasks: MutableList<Task>
)