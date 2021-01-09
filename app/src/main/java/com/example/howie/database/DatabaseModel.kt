package com.example.howie.database

import com.example.howie.TaskEntity
import com.example.howie.core.TaskList

data class DatabaseModel(
    val taskEntities: List<TaskEntity>,
    val taskListEntities: List<TaskListEntity>
)

fun List<TaskList>.toDatabaseModel() = DatabaseModel(
    // TODO: Implement
    listOf(),
    listOf()
)

fun DatabaseModel.toDomainModel() = listOf<TaskList>(
    // TODO: Implement
)