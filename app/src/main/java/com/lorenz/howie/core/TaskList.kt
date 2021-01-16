package com.lorenz.howie.core

data class TaskList (
    val name: String,
    val tasks: MutableList<Task>
)