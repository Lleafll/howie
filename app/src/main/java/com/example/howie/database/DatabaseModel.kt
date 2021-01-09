package com.example.howie.database

import com.example.howie.TaskEntity
import com.example.howie.core.Task
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

fun DatabaseModel.toDomainModel(): List<TaskList> {
    return taskListEntities.map { taskListEntity ->
        TaskList(taskListEntity.name, taskEntities.map { it.toTask() })
    }
}

private fun TaskEntity.toTask(): Task {
    return Task(name, importance, due, snoozed, schedule, archived)
}